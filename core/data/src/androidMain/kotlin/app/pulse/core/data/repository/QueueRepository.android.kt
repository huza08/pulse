package app.pulse.core.data.repository

import app.pulse.core.data.models.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QueueRepositoryImpl : QueueRepository {
    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    override val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    override val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    override suspend fun add(song: Song) {
        _queue.update { it + song }
    }

    override suspend fun remove(index: Int) {
        _queue.update { it.toMutableList().apply { removeAt(index) } }
    }

    override suspend fun move(from: Int, to: Int) {
        _queue.update {
            it.toMutableList().apply {
                val item = removeAt(from)
                add(to, item)
            }
        }
    }

    override suspend fun clear() {
        _queue.value = emptyList()
        _currentIndex.value = 0
    }

    override suspend fun setIndex(index: Int) {
        _currentIndex.value = index
    }
}
