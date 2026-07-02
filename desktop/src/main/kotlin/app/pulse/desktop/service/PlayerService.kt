package app.pulse.desktop.service

import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.models.PlayerResponse
import app.pulse.providers.innertube.models.bodies.PlayerBody
import app.pulse.providers.innertube.requests.player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.math.log10
import kotlin.math.roundToLong

private val logFmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

private fun log(msg: String) {
    println("[${LocalTime.now().format(logFmt)}] [PlayerService] $msg")
}

/** return value from playViaStream to inform the caller what action to take. */
private enum class StreamEnd {
    /** stream ended naturally at expected position → safe to advance to next. */
    COMPLETED,
    /** stream did not complete (cancelled/paused) → caller does nothing. */
    INTERRUPTED,
    /** cache-hit stream ended naturally but position << duration → cache was incomplete. */
    INCOMPLETE_CACHE
}

class PlayerService {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var playbackJob: Job? = null
    private var backgroundDownloadJob: Job? = null
    private var line: SourceDataLine? = null
    private var stream: AudioInputStream? = null
    private var decodeProcess: Process? = null
    private var ytDlpProcess: Process? = null
    private var backgroundProcess: Process? = null

    @Volatile
    private var isPaused = false
    private var bytesPerMs = 0.0
    @Volatile
    private var seekBaseMs = 0L
    private var currentVideoId: String? = null
    private var currentPlayerResponse: PlayerResponse? = null
    @Volatile
    private var lastSeekMs = 0L

    companion object {
        private val cacheDir = File(System.getProperty("java.io.tmpdir"), "pulse-cache")

        /** max cache size in bytes (500 MB). */
        private const val MAX_CACHE_BYTES = 500L * 1024 * 1024

        /** max age in milliseconds (24 hours). */
        private const val MAX_CACHE_AGE_MS = 24L * 60 * 60 * 1000

        /** run cache cleanup on JVM load. */
        fun cleanCache() {
            val dir = cacheDir
            if (!dir.isDirectory) return

            val now = System.currentTimeMillis()
            val files = dir.listFiles()?.filter { it.isFile && !it.name.endsWith(".done") }?.toMutableList()
                ?: return

            val expired = mutableListOf<File>()
            val kept = mutableListOf<File>()
            for (f in files) {
                if (now - f.lastModified() > MAX_CACHE_AGE_MS) expired.add(f)
                else kept.add(f)
            }
            for (f in expired) {
                File(dir, "${f.name}.done").delete()
                f.delete()
            }
            log("cache: deleted ${expired.size} expired files")

            var totalBytes = kept.sumOf { it.length() }
            if (totalBytes <= MAX_CACHE_BYTES) return

            kept.sortBy { it.lastModified() }
            val toDelete = mutableListOf<File>()
            for (f in kept) {
                if (totalBytes <= MAX_CACHE_BYTES) break
                toDelete.add(f)
                totalBytes -= f.length()
            }
            for (f in toDelete) {
                File(dir, "${f.name}.done").delete()
                f.delete()
            }
            log("cache: deleted ${toDelete.size} oldest files to stay under ${MAX_CACHE_BYTES / (1024*1024)} MB")
        }
    }

    init {
        cleanCache()
    }

    // -- Queue management (mirrors Android pattern) ----------------------------

    fun play(song: Innertube.SongItem) {
        playFromQueue(listOf(song), index = 0)
    }

    fun playFromQueue(queue: List<Innertube.SongItem>, index: Int) {
        if (queue.isEmpty()) return
        val idx = index.coerceIn(0, queue.lastIndex)
        _state.update { it.copy(queue = queue, currentIndex = idx) }
        playInternal(queue[idx])
    }

    fun playNext() {
        val s = _state.value
        val nextIdx = when (s.loopMode) {
            LoopMode.ONE -> s.currentIndex
            LoopMode.ALL -> {
                val n = s.currentIndex + 1
                if (n >= s.queue.size) 0 else n
            }
            LoopMode.NONE -> s.currentIndex + 1
        }
        if (nextIdx !in s.queue.indices) {
            if (s.currentSong != null) endSong() else stop()
            return
        }
        _state.update { it.copy(currentIndex = nextIdx) }
        playInternal(s.queue[nextIdx])
    }

    fun playPrevious() {
        val s = _state.value
        val prevIdx = when (s.loopMode) {
            LoopMode.ONE -> s.currentIndex
            LoopMode.ALL -> {
                val p = s.currentIndex - 1
                if (p < 0) s.queue.lastIndex else p
            }
            LoopMode.NONE -> (s.currentIndex - 1).coerceAtLeast(0)
        }
        _state.update { it.copy(currentIndex = prevIdx) }
        playInternal(s.queue[prevIdx])
    }

    fun enqueue(song: Innertube.SongItem) {
        _state.update { it.copy(queue = it.queue + song) }
    }

    fun addNext(song: Innertube.SongItem) {
        _state.update { s ->
            val idx = (s.currentIndex + 1).coerceIn(0, s.queue.size)
            val q = s.queue.toMutableList().apply { add(idx, song) }
            s.copy(queue = q)
        }
    }

    fun setLoopMode(mode: LoopMode) {
        _state.update { it.copy(loopMode = mode) }
    }

    fun cycleLoopMode() {
        _state.update { s ->
            val next = when (s.loopMode) {
                LoopMode.NONE -> LoopMode.ONE
                LoopMode.ONE -> LoopMode.ALL
                LoopMode.ALL -> LoopMode.NONE
            }
            s.copy(loopMode = next)
        }
    }


    private fun playInternal(song: Innertube.SongItem) {
        playbackJob?.cancel()
        backgroundDownloadJob?.cancel()
        backgroundProcess?.destroyForcibly()
        backgroundProcess = null
        stopAudio()

        _state.update {
            PlaybackState(
                queue = it.queue,
                currentIndex = it.currentIndex,
                loopMode = it.loopMode,
                currentSong = song,
                volume = it.volume,
                isLoading = true
            )
        }

        currentVideoId = song.info?.endpoint?.videoId
        if (currentVideoId == null) {
            _state.update { it.copy(isLoading = false, error = "No video ID") }
            return
        }
        log("play: ${song.info?.name} (id=$currentVideoId)")

        playbackJob = scope.launch {
            try {
                val response = Innertube.player(PlayerBody(videoId = currentVideoId!!))
                currentPlayerResponse = response?.getOrNull()
                startPipeline(currentVideoId!!, currentPlayerResponse, startMs = 0)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log("play error: ${e.message}")
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun pause() {
        if (!_state.value.isPlaying) return
        isPaused = true
        runCatching { line?.stop() }
        _state.update { it.copy(isPlaying = false) }
        log("pause")
    }

    fun resume() {
        val s = _state.value
        if (s.isPlaying) return
        val song = s.currentSong ?: return

        if (s.isEnded) {
            log("resume from ended → restart")
            play(song)
            return
        }

        if (!isPaused) {
            play(song)
            return
        }

        isPaused = false
        runCatching { line?.start() }
        _state.update { it.copy(isPlaying = true) }
        log("resume")
    }

    fun stop() {
        playbackJob?.cancel()
        backgroundDownloadJob?.cancel()
        backgroundProcess?.destroyForcibly()
        backgroundProcess = null
        playbackJob = null
        stopAudio()
        _state.update {
            PlaybackState(
                queue = it.queue,
                loopMode = it.loopMode,
                volume = it.volume
            )
        }
        log("stop")
    }

    fun seek(positionMs: Long) {
        val vid = currentVideoId ?: return
        val resp = currentPlayerResponse
        val dur = _state.value.durationMs

        _state.update { it.copy(currentPositionMs = positionMs) }

        if (dur > 0 && positionMs >= dur) {
            log("seek $positionMs ≥ $dur → endSong")
            advanceOrStop()
            return
        }

        val now = System.currentTimeMillis()
        if (now - lastSeekMs < 500L) {
            log("seek $positionMs debounced")
            return
        }
        lastSeekMs = now

        log("seek $positionMs")
        startPipeline(vid, resp, startMs = positionMs)
    }

    fun skipForward(sec: Int = 10) {
        val cur = _state.value.currentPositionMs
        val dur = _state.value.durationMs
        val maxSeek = (dur - 1000L).coerceAtLeast(0L)
        val target = (cur + sec * 1000L).coerceIn(0L, maxSeek)
        log("skipForward $sec → $target")
        seek(target)
    }

    fun skipBackward(sec: Int = 10) {
        val cur = _state.value.currentPositionMs
        val target = (cur - sec * 1000L).coerceAtLeast(0L)
        log("skipBackward $sec → $target")
        seek(target)
    }

    fun setVolume(volume: Float) {
        _state.update { it.copy(volume = volume.coerceIn(0f, 1f)) }
        applyVolume()
    }

    private fun applyVolume() {
        val audioLine = line ?: return
        if (!audioLine.isOpen) return
        try {
            val gain = audioLine.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl ?: return
            val vol = _state.value.volume
            val minDb = gain.minimum
            val maxDb = gain.maximum
            val db = if (vol <= 0f) minDb
            else (20f * log10(vol)).coerceIn(minDb, maxDb)
            gain.value = db
        } catch (_: IllegalArgumentException) { }
    }
    private fun startPipeline(videoId: String, playerResponse: PlayerResponse?, startMs: Long) {
        playbackJob?.cancel()
        stopAudio()

        seekBaseMs = startMs

        val dur = playerResponse?.streamingData?.highestQualityFormat?.approxDurationMs
        if (dur != null && startMs >= dur) {
            _state.update { it.copy(isLoading = false, currentPositionMs = dur, isPlaying = false) }
            advanceOrStop()
            return
        }

        val pipelineId = System.identityHashCode(this).toString() + "-" + System.nanoTime()
        log("pipeline[$pipelineId] start video=$videoId startMs=$startMs")

        playbackJob = scope.launch {
            try {
                val format = playerResponse?.streamingData?.highestQualityFormat
                format?.approxDurationMs?.let { d ->
                    _state.update { it.copy(durationMs = d) }
                    log("pipeline[$pipelineId] duration=${d}ms")
                }

                val ytDlpBin = NativeBinaries.ytDlp()
                val ffmpegBin = NativeBinaries.ffmpeg()
                val url = "https://www.youtube.com/watch?v=$videoId"

                val cacheFile = File(cacheDir, videoId)
                val cacheDone = File(cacheDir, "${videoId}.done")

                if (cacheDone.exists() && cacheFile.exists() && cacheFile.length() > 0) {
                    log("pipeline[$pipelineId] cache HIT")
                    val cmd = if (startMs > 0) {
                        listOf(
                            ffmpegBin, "-loglevel", "error",
                            "-ss", (startMs / 1000f).toString(),
                            "-i", cacheFile.absolutePath,
                            "-acodec", "pcm_s16le", "-f", "wav", "-"
                        )
                    } else {
                        listOf(
                            ffmpegBin, "-loglevel", "error",
                            "-i", cacheFile.absolutePath,
                            "-acodec", "pcm_s16le", "-f", "wav", "-"
                        )
                    }
                    val pb = ProcessBuilder(cmd)
                    pb.redirectError(ProcessBuilder.Redirect.INHERIT)
                    decodeProcess = pb.start()
                    when (playViaStream(decodeProcess!!.inputStream, false)) {
                        StreamEnd.COMPLETED -> advanceToNext()
                        StreamEnd.INCOMPLETE_CACHE -> {
                            log("pipeline[$pipelineId] cache was INCOMPLETE, re-downloading")
                            cacheDone.delete()
                            cacheFile.delete()
                            val song = _state.value.currentSong ?: return@launch
                            playInternal(song)
                        }
                        StreamEnd.INTERRUPTED -> { /* seek/stop handled elsewhere */ }
                    }
                    return@launch
                }

                if (startMs == 0L) {
                    log("pipeline[$pipelineId] download FULL, tee to cache")
                    cacheDir.mkdirs()
                    val ytPb = ProcessBuilder(ytDlpBin, "-f", "bestaudio", "-o", "-", "-q", url)
                    ytPb.redirectError(ProcessBuilder.Redirect.DISCARD)
                    val ytDlp = ytPb.start()
                    ytDlpProcess = ytDlp

                    val ffPb = ProcessBuilder(
                        ffmpegBin, "-loglevel", "error",
                        "-i", "-",
                        "-acodec", "pcm_s16le", "-f", "wav", "-"
                    )
                    ffPb.redirectError(ProcessBuilder.Redirect.INHERIT)
                    val ffmpeg = ffPb.start()
                    decodeProcess = ffmpeg

                    startTeeThread(ytDlp, ffmpeg, cacheFile, videoId, pipelineId, cache = true)

                    if (playViaStream(ffmpeg.inputStream, true) == StreamEnd.COMPLETED) {
                        advanceToNext()
                    }
                    return@launch
                }

                val startSec = startMs / 1000
                val endSec = format?.approxDurationMs?.let { it / 1000 } ?: 99999L
                log("pipeline[$pipelineId] download section ${startSec}-${endSec}s (no cache)")

                // Start background download of full song so future seeks are instant
                if (backgroundDownloadJob?.isActive != true) {
                    val cacheDone = File(cacheDir, "${videoId}.done")
                    if (!cacheDone.exists()) {
                        log("pipeline[$pipelineId] starting background download")
                        backgroundDownloadJob = scope.launch(Dispatchers.IO) {
                            downloadFullSong(videoId, pipelineId)
                        }
                    }
                }

                val ytPb = ProcessBuilder(
                    ytDlpBin, "-f", "bestaudio", "-o", "-", "-q",
                    "--download-sections", "*${startSec}-${endSec}",
                    url
                )
                ytPb.redirectError(ProcessBuilder.Redirect.DISCARD)
                ytDlpProcess = ytPb.start()

                val ffPb = ProcessBuilder(
                    ffmpegBin, "-loglevel", "error",
                    "-i", "-",
                    "-acodec", "pcm_s16le", "-f", "wav", "-"
                )
                ffPb.redirectError(ProcessBuilder.Redirect.INHERIT)
                decodeProcess = ffPb.start()

                val ytLocal = ytDlpProcess!!
                val ffLocal = decodeProcess!!
                startTeeThread(ytLocal, ffLocal, null, videoId, pipelineId, cache = false)

                if (playViaStream(ffLocal.inputStream, false) == StreamEnd.COMPLETED) {
                    advanceToNext()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log("pipeline[$pipelineId] error: ${e.message}")
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun downloadFullSong(videoId: String, pipelineId: String) = withContext(Dispatchers.IO) {
        val ytDlpBin = NativeBinaries.ytDlp()
        val tmpFile = File(cacheDir, "${videoId}.tmp")
        val cacheFile = File(cacheDir, videoId)
        val cacheDone = File(cacheDir, "${videoId}.done")

        var proc: Process? = null
        try {
            cacheDir.mkdirs()
            val pb = ProcessBuilder(
                ytDlpBin, "-f", "bestaudio", "-q", "--output", tmpFile.absolutePath,
                "https://www.youtube.com/watch?v=$videoId"
            )
            pb.redirectError(ProcessBuilder.Redirect.DISCARD)
            val p = pb.start()
            proc = p
            backgroundProcess = p
            p.waitFor()

            if (p.exitValue() == 0 && tmpFile.exists() && tmpFile.length() > 0) {
                tmpFile.renameTo(cacheFile)
                cacheDone.createNewFile()
                log("bgdl[$pipelineId] complete: $videoId (${cacheFile.length()} bytes)")
            } else {
                tmpFile.delete()
                log("bgdl[$pipelineId] failed exit=${p.exitValue()}: $videoId")
            }
        } catch (e: CancellationException) {
            proc?.destroyForcibly()
            tmpFile.delete()
            log("bgdl[$pipelineId] cancelled: $videoId")
            throw e
        } catch (e: Exception) {
            tmpFile.delete()
            log("bgdl[$pipelineId] error: ${e.message}")
        } finally {
            backgroundProcess = null
        }
    }

    private fun startTeeThread(
        ytDlp: Process,
        ffmpeg: Process,
        cacheFile: File?,
        videoId: String,
        pipelineId: String,
        cache: Boolean
    ) {
        val localCacheFile = cacheFile  // capture local
        Thread {
            val cacheOut = if (cache && localCacheFile != null) {
                runCatching { FileOutputStream(localCacheFile) }.getOrNull()
            } else null

            try {
                ytDlp.inputStream.use { input ->
                    ffmpeg.outputStream.use { output ->
                        val buf = ByteArray(8192)
                        while (true) {
                            val n = input.read(buf)
                            if (n == -1) break
                            runCatching { output.write(buf, 0, n) }
                            runCatching { cacheOut?.write(buf, 0, n) }
                        }
                    }
                }
            } finally {
                runCatching { cacheOut?.close() }
            }
        }.apply { isDaemon = true }.start()

    }

    private suspend fun playViaStream(inputStream: java.io.InputStream, isFullDownload: Boolean): StreamEnd =
        withContext(Dispatchers.IO) {
        log("playViaStream start")
        val audioStream = AudioSystem.getAudioInputStream(BufferedInputStream(inputStream))
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
        bytesPerMs = decodedFormat.sampleRate * decodedFormat.frameSize / 1000.0

        val lineInfo = DataLine.Info(SourceDataLine::class.java, decodedFormat)
        val audioLine = AudioSystem.getLine(lineInfo) as SourceDataLine
        line = audioLine

        audioLine.open(decodedFormat)
        applyVolume()
        audioLine.start()

        val decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream)
        val buffer = ByteArray(4096)
        var totalBytes = 0L

        _state.update { it.copy(isLoading = false, isPlaying = true) }
        isPaused = false

        while (isActive) {
            if (isPaused) {
                delay(100)
                continue
            }

            val bytesRead = decodedStream.read(buffer)
            if (bytesRead == -1) break

            audioLine.write(buffer, 0, bytesRead)
            totalBytes += bytesRead

            if (bytesPerMs > 0) {
                _state.update {
                    it.copy(currentPositionMs = seekBaseMs + (totalBytes / bytesPerMs).roundToLong())
                }
            }
        }

        if (audioLine.isOpen) {
            audioLine.drain()
            audioLine.close()
        }
        _state.update { it.copy(isPlaying = false) }

        val completed = isActive && !isPaused
        val finalPos = seekBaseMs + (totalBytes / bytesPerMs).roundToLong()
        val dur = _state.value.durationMs

        val result = when {
            !completed -> {
                val why = if (!isActive) "interrupted (cancelled)" else "paused"
                log("playViaStream end: $why totalBytes=$totalBytes")
                StreamEnd.INTERRUPTED
            }
            !isFullDownload && dur > 0 && finalPos < dur - 5000 -> {
                log("playViaStream end: INCOMPLETE CACHE (pos=$finalPos < dur=$dur)")
                StreamEnd.INCOMPLETE_CACHE
            }
            else -> {
                log("playViaStream end: completed (natural EOF) totalBytes=$totalBytes")
                StreamEnd.COMPLETED
            }
        }

        // if this was a full-song download that completed naturally, mark cache as done.
        if (completed && isFullDownload) {
            val cacheDone = File(cacheDir, "${currentVideoId}.done")
            if (cacheDone.parentFile.isDirectory) {
                runCatching { cacheDone.createNewFile() }
                log("cache DONE: $currentVideoId")
            }
        }

        return@withContext result
    }

    private fun endSong() {
        playbackJob?.cancel()
        playbackJob = null
        stopAudio()
        _state.update {
            it.copy(
                isPlaying = false,
                isEnded = true,
                isLoading = false,
                currentPositionMs = it.durationMs
            )
        }
        log("endSong")
    }

    private fun advanceOrStop() {
        val s = _state.value
        val nextAction = when (s.loopMode) {
            LoopMode.NONE -> if (s.currentIndex + 1 < s.queue.size) "next-in-queue" else "endSong"
            LoopMode.ONE -> "replay"
            LoopMode.ALL -> "wrap-to-start"
        }
        log("advanceOrStop: action=$nextAction loopMode=${s.loopMode}")
        when (s.loopMode) {
            LoopMode.NONE -> {
                val next = s.currentIndex + 1
                if (next < s.queue.size) {
                    _state.update { it.copy(currentIndex = next) }
                    playInternal(s.queue[next])
                } else {
                    endSong()
                }
            }
            LoopMode.ONE -> {
                s.currentSong?.let { playInternal(it) }
            }
            LoopMode.ALL -> {
                val next = (s.currentIndex + 1) % s.queue.size
                _state.update { it.copy(currentIndex = next) }
                playInternal(s.queue[next])
            }
        }
    }

    private fun advanceToNext() = advanceOrStop()

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
        backgroundDownloadJob?.cancel()
        scope.cancel()
        log("dispose")
    }
}
