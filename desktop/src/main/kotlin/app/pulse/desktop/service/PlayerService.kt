package app.pulse.desktop.service

import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.models.PlayerResponse
import app.pulse.providers.innertube.models.bodies.PlayerBody
import app.pulse.providers.innertube.requests.player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import kotlin.math.roundToLong

class PlayerService {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var playbackJob: Job? = null
    private var line: SourceDataLine? = null
    private var stream: AudioInputStream? = null
    private var decodeProcess: Process? = null

    private var isPaused = false
    private var bytesPerMs = 0.0

    fun play(song: Innertube.SongItem) {
        playbackJob?.cancel()
        stopAudio()

        _state.update { PlaybackState(currentSong = song, isLoading = true) }

        playbackJob = scope.launch {
            try {
                val videoId = song.info?.endpoint?.videoId
                    ?: throw Exception("No video ID for this song")

                // Get PlayerResponse once — used for both duration and streaming URL
                val response = Innertube.player(PlayerBody(videoId = videoId))
                val playerResponse = response?.getOrNull()

                // Innertube gives direct URLs for most videos (signatureCipher=null)
                // Pipe through bundled ffmpeg → WAV → javax.sound
                // Fall back to yt-dlp only when URL is ciphered
                resolveAndPlay(videoId, playerResponse)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun pause() {
        isPaused = true
        _state.update { it.copy(isPlaying = false) }
    }

    fun resume() {
        isPaused = false
        _state.update { it.copy(isPlaying = true) }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        stopAudio()
        _state.update { PlaybackState() }
    }

    fun seek(positionMs: Long) {
        // ponytail: seeking not yet implemented for streamed playback
        _state.update { it.copy(currentPositionMs = positionMs) }
    }

    fun setVolume(volume: Float) {
        _state.update { it.copy(volume = volume.coerceIn(0f, 1f)) }
    }

    private var ytDlpProcess: Process? = null

    private suspend fun resolveAndPlay(videoId: String, playerResponse: PlayerResponse?) {
        val format = playerResponse?.streamingData?.highestQualityFormat

        format?.approxDurationMs?.let { dur ->
            _state.update { it.copy(durationMs = dur) }
        }

        // yt-dlp handles YouTube CDN auth (cookies, n-param, session) reliably.
        // Direct Innertube URLs fail with 403 because googlevideo CDN validates
        // more than just User-Agent (cookies, n-param resolution, etc.).
        val ytDlpBin = NativeBinaries.ytDlp()
        val ffmpegBin = NativeBinaries.ffmpeg()

        val url = "https://www.youtube.com/watch?v=$videoId"

        // Step 1: yt-dlp downloads best audio to stdout
        val ytDlpCmd = listOf(ytDlpBin, "-f", "bestaudio", "-o", "-", "-q", url)
        val ytDlpPb = ProcessBuilder(ytDlpCmd)
        ytDlpPb.redirectError(ProcessBuilder.Redirect.DISCARD) // suppress deno warnings etc.
        val ytDlp = ytDlpPb.start()
        ytDlpProcess = ytDlp

        // Step 2: ffmpeg converts raw audio → PCM WAV on stdout
        // yt-dlp's stdout → ffmpeg's stdin via pipe thread
        val ffmpegCmd = listOf(ffmpegBin, "-loglevel", "error", "-i", "-", "-acodec", "pcm_s16le", "-f", "wav", "-")
        val ffmpegPb = ProcessBuilder(ffmpegCmd)
        ffmpegPb.redirectError(ProcessBuilder.Redirect.INHERIT)
        val ffmpeg = ffmpegPb.start()
        decodeProcess = ffmpeg

        // Pipe yt-dlp stdout → ffmpeg stdin (daemon thread, auto-killed on exit)
        Thread {
            ytDlp.inputStream.use { input ->
                ffmpeg.outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            // Signal EOF to ffmpeg when yt-dlp finishes
            runCatching { ffmpeg.outputStream.close() }
        }.apply { isDaemon = true }.start()

        playViaStream(ffmpeg.inputStream)
    }

    private suspend fun playViaStream(inputStream: java.io.InputStream) = withContext(Dispatchers.IO) {
        val audioStream = AudioSystem.getAudioInputStream(
            BufferedInputStream(inputStream)
        )
        stream = audioStream

        val fmt = audioStream.format
        val decodedFormat = AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            fmt.sampleRate,
            16,
            fmt.channels,
            fmt.channels * 2,
            fmt.sampleRate,
            false
        )
        bytesPerMs = (decodedFormat.sampleRate * decodedFormat.frameSize / 1000.0)

        val lineInfo = DataLine.Info(SourceDataLine::class.java, decodedFormat)
        val audioLine = AudioSystem.getLine(lineInfo) as SourceDataLine
        line = audioLine

        audioLine.open(decodedFormat)
        audioLine.start()

        val decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        var totalBytes = 0L

        _state.update { it.copy(isLoading = false, isPlaying = true) }

        while (decodedStream.read(buffer).also { bytesRead = it } != -1) {
            if (!_state.value.isPlaying && isPaused) {
                while (isPaused && playbackJob?.isActive == true) {
                    delay(100)
                }
                if (playbackJob?.isActive != true) break
            }

            audioLine.write(buffer, 0, bytesRead)
            totalBytes += bytesRead

            if (bytesPerMs > 0) {
                _state.update { it.copy(currentPositionMs = (totalBytes / bytesPerMs).roundToLong()) }
            }
        }

        audioLine.drain()
        audioLine.close()
        _state.update { it.copy(isPlaying = false) }
    }

    private fun stopAudio() {
        runCatching { decodeProcess?.destroyForcibly() }
        decodeProcess = null
        runCatching { ytDlpProcess?.destroyForcibly() }
        ytDlpProcess = null
        runCatching { line?.stop() }
        runCatching { line?.close() }
        runCatching { stream?.close() }
        line = null
        stream = null
        isPaused = false
    }

    fun dispose() {
        stop()
        scope.cancel()
    }
}
