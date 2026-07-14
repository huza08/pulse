package app.pulse.core.data.models

import kotlinx.serialization.Serializable

@Serializable
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
)
