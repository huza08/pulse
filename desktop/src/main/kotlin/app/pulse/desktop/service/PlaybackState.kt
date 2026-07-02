package app.pulse.desktop.service

import app.pulse.providers.innertube.Innertube

enum class LoopMode {
    NONE,   // stop at queue end
    ONE,    // repeat current track
    ALL     // repeat entire queue
}

data class PlaybackState(
    val currentSong: Innertube.SongItem? = null,
    val queue: List<Innertube.SongItem> = emptyList(),
    val currentIndex: Int = -1,
    val loopMode: LoopMode = LoopMode.NONE,
    val isPlaying: Boolean = false,
    val isEnded: Boolean = false,
    val isLoading: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val volume: Float = 1f,
    val error: String? = null
) {
    val hasNext: Boolean get() = when (loopMode) {
        LoopMode.NONE -> currentIndex < queue.lastIndex
        LoopMode.ONE, LoopMode.ALL -> true
    }

    val hasPrevious: Boolean get() = currentIndex > 0 || loopMode == LoopMode.ALL
}
