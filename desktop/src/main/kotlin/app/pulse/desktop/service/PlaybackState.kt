package app.pulse.desktop.service

import app.pulse.core.data.models.Song
import app.pulse.core.data.models.LoopMode

data class PlaybackState(
    val currentSong: Song? = null,
    val queue: List<Song> = emptyList(),
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
