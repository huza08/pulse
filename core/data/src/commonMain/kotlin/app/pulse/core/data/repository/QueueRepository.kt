package app.pulse.core.data.repository

import app.pulse.core.data.models.Song
import kotlinx.coroutines.flow.StateFlow

interface QueueRepository {
    val queue: StateFlow<List<Song>>
    val currentIndex: StateFlow<Int>

    suspend fun add(song: Song)
    suspend fun remove(index: Int)
    suspend fun move(from: Int, to: Int)
    suspend fun clear()
    suspend fun setIndex(index: Int)
}
