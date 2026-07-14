package app.pulse.core.data.repository

import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import app.pulse.core.data.models.PlaybackState
import app.pulse.core.data.models.Song
import app.pulse.core.data.utils.SongBundleAccessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@OptIn(UnstableApi::class)
class PlayerRepositoryImpl(
    private val exoPlayer: ExoPlayer
) : PlayerRepository {
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state.asStateFlow()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                _state.update { it.copy(currentSong = mediaItem?.toSong()) }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updatePosition()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePosition()
            }
        })
    }

    private fun updatePosition() {
        _state.update {
            it.copy(
                currentPositionMs = exoPlayer.currentPosition,
                durationMs = if (exoPlayer.duration > 0) exoPlayer.duration else 0
            )
        }
    }

    override suspend fun play(song: Song) {
        val mediaItem = song.toMediaItem()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override suspend fun pause() = exoPlayer.pause()
    override suspend fun resume() = exoPlayer.play()
    override suspend fun stop() = exoPlayer.stop()
    override suspend fun seekTo(positionMs: Long) = exoPlayer.seekTo(positionMs)
    override suspend fun setVolume(volume: Float) {
        exoPlayer.volume = volume
    }

    private fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(id.toUri())
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artistsText)
                    .setArtworkUri(thumbnailUrl?.toUri())
                    .setExtras(
                        SongBundleAccessor.bundle {
                            durationText = this@toMediaItem.durationText
                            explicit = this@toMediaItem.explicit
                        }
                    )
                    .build()
            )
            .build()
    }

    private fun MediaItem.toSong(): Song {
        val extras = mediaMetadata.extras
        val bundle = extras?.let { SongBundleAccessor(it) }
        return Song(
            id = mediaId,
            title = mediaMetadata.title?.toString().orEmpty(),
            artistsText = mediaMetadata.artist?.toString(),
            thumbnailUrl = mediaMetadata.artworkUri?.toString(),
            durationText = bundle?.durationText,
            explicit = bundle?.explicit ?: false
        )
    }
}
