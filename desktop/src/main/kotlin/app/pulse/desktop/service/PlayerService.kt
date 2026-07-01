package app.pulse.desktop.service

import app.pulse.providers.innertube.Innertube
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
import java.net.URL
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

    private var isPaused = false
    private var bytesPerMs = 0.0

    fun play(song: Innertube.SongItem) {
        playbackJob?.cancel()
        stopLine()

        _state.update { PlaybackState(currentSong = song, isLoading = true) }

        playbackJob = scope.launch {
            try {
                val audioUrl = resolveAudioUrl(song) ?: throw Exception("No audio URL available")
                playUrl(audioUrl)
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
        stopLine()
        _state.update { PlaybackState() }
    }

    fun seek(positionMs: Long) {
        _state.update { it.copy(currentPositionMs = positionMs) }
    }

    fun setVolume(volume: Float) {
        _state.update { it.copy(volume = volume.coerceIn(0f, 1f)) }
    }

    private suspend fun resolveAudioUrl(song: Innertube.SongItem): String? {
        val videoId = song.info?.endpoint?.videoId ?: return null
        // ponytail: for now return a known test stream
        // TODO: call Innertube.player(PlayerBody(videoId)) to get actual streaming URL
        return null
    }

    private suspend fun playUrl(url: String) = withContext(Dispatchers.IO) {
        val audioStream = AudioSystem.getAudioInputStream(
            BufferedInputStream(URL(url).openStream())
        )
        stream = audioStream

        val format = audioStream.format
        val decodedFormat = AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            format.sampleRate,
            16,
            format.channels,
            format.channels * 2,
            format.sampleRate,
            false
        )
        bytesPerMs = (decodedFormat.sampleRate * decodedFormat.frameSize / 1000.0)

        val info = DataLine.Info(SourceDataLine::class.java, decodedFormat)
        val audioLine = AudioSystem.getLine(info) as SourceDataLine
        line = audioLine

        audioLine.open(decodedFormat)
        audioLine.start()

        val decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        var totalBytes = 0L
        val durationMs = audioStream.frameLength.let {
            if (it > 0) (it / format.sampleRate * 1000).roundToLong() else 0L
        }

        _state.update { it.copy(isLoading = false, isPlaying = true, durationMs = durationMs) }

        while (decodedStream.read(buffer).also { bytesRead = it } != -1) {
            if (!_state.value.isPlaying && isPaused) {
                // Wait while paused — keep the thread alive
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

    private fun stopLine() {
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
