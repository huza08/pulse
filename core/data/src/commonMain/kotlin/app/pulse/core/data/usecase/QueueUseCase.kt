package app.pulse.core.data.usecase

import app.pulse.core.data.models.Song
import app.pulse.core.data.repository.PlayerRepository
import app.pulse.core.data.repository.QueueRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class QueueUseCase(
    private val playerRepository: PlayerRepository,
    private val queueRepository: QueueRepository
) {
    val queue = queueRepository.queue
    val currentIndex = queueRepository.currentIndex

    val currentSong = combine(queue, currentIndex) { q, i ->
        q.getOrNull(i)
    }

    suspend fun playNext() {
        val nextIndex = currentIndex.value + 1
        if (nextIndex < queue.value.size) {
            queueRepository.setIndex(nextIndex)
            queue.value.getOrNull(nextIndex)?.let { playerRepository.play(it) }
        }
    }

    suspend fun playPrevious() {
        val prevIndex = currentIndex.value - 1
        if (prevIndex >= 0) {
            queueRepository.setIndex(prevIndex)
            queue.value.getOrNull(prevIndex)?.let { playerRepository.play(it) }
        }
    }

    suspend fun addAndPlay(song: Song) {
        queueRepository.add(song)
        queueRepository.setIndex(queue.value.size - 1)
        playerRepository.play(song)
    }
}
