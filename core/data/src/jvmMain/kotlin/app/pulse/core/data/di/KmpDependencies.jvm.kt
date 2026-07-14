package app.pulse.core.data.di

import app.pulse.core.data.repository.PlayerRepository
import app.pulse.core.data.repository.PlayerRepositoryImpl
import app.pulse.core.data.repository.QueueRepository
import app.pulse.core.data.repository.QueueRepositoryImpl

actual fun providePlayerRepository(): PlayerRepository {
    return PlayerRepositoryImpl()
}

actual fun provideQueueRepository(): QueueRepository {
    return QueueRepositoryImpl()
}
