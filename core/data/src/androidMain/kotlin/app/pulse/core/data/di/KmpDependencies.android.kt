package app.pulse.core.data.di

import app.pulse.core.data.repository.PlayerRepository
import app.pulse.core.data.repository.PlayerRepositoryImpl
import app.pulse.core.data.repository.QueueRepository
import app.pulse.core.data.repository.QueueRepositoryImpl
import androidx.media3.exoplayer.ExoPlayer

private var exoPlayerInstance: ExoPlayer? = null

fun setExoPlayer(exoPlayer: ExoPlayer) {
    exoPlayerInstance = exoPlayer
}

actual fun providePlayerRepository(): PlayerRepository {
    return PlayerRepositoryImpl(exoPlayerInstance ?: error("ExoPlayer not set"))
}

actual fun provideQueueRepository(): QueueRepository {
    return QueueRepositoryImpl()
}
