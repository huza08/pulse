package app.pulse.desktop.service

import app.pulse.providers.innertube.Innertube

data class PlaybackState(
    val currentSong: Innertube.SongItem? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val volume: Float = 1f,
    val error: String? = null
)
