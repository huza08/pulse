package app.pulse.core.data.repository

import app.pulse.core.data.models.PlaybackState
import app.pulse.core.data.models.Song
import kotlinx.coroutines.flow.StateFlow

interface PlayerRepository {
    val state: StateFlow<PlaybackState>

    suspend fun play(song: Song)
    suspend fun pause()
    suspend fun resume()
    suspend fun stop()
    suspend fun seekTo(positionMs: Long)
    suspend fun setVolume(volume: Float)
}
