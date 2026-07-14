package app.pulse.core.data.di

import app.pulse.core.data.repository.PlayerRepository
import app.pulse.core.data.repository.QueueRepository
import app.pulse.core.data.usecase.QueueUseCase

expect fun providePlayerRepository(): PlayerRepository
expect fun provideQueueRepository(): QueueRepository

object KmpDependencies {
    val playerRepository by lazy { providePlayerRepository() }
    val queueRepository by lazy { provideQueueRepository() }

    val queueUseCase by lazy { QueueUseCase(playerRepository, queueRepository) }
}
