package app.pulse.android.ui.screens.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import app.pulse.android.R
import app.pulse.android.models.ui.UiMedia
import app.pulse.android.models.ui.toUiMedia
import app.pulse.android.preferences.PlayerPreferences
import app.pulse.android.service.PlayerService
import app.pulse.android.ui.components.SeekBar
import app.pulse.android.utils.bold
import app.pulse.android.utils.forceSeekToNext
import app.pulse.android.utils.forceSeekToPrevious
import app.pulse.android.utils.secondary
import app.pulse.android.utils.semiBold
import app.pulse.android.utils.shouldBePlaying
import app.pulse.android.utils.rememberIsBuffering
import app.pulse.android.utils.thumbnail
import app.pulse.core.ui.Dimensions
import app.pulse.core.ui.LocalAppearance
import app.pulse.core.ui.favoritesIcon
import app.pulse.core.ui.utils.px
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlin.math.roundToInt

@Composable
fun NewLayoutContent(
    mediaItem: MediaItem?,
    binder: PlayerService.Binder?,
    likedAt: Long?,
    setLikedAt: (Long?) -> Unit,
    position: Long,
    duration: Long,
    onLyricsClick: () -> Unit,
    onQueueClick: () -> Unit,
    onMenuLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (colorPalette, typography) = LocalAppearance.current
    val context = LocalContext.current
    val player = binder?.player ?: return
    val shouldBePlaying = player.shouldBePlaying
    val isBuffering = player.rememberIsBuffering() ||
        (shouldBePlaying && player.playbackState != Player.STATE_READY)

    val metadata = remember(mediaItem) { mediaItem?.mediaMetadata }
    val artworkUri = remember(mediaItem) {
        val thumbSize = with(context.resources.displayMetrics) {
            maxOf(widthPixels, heightPixels)
        }
        mediaItem?.mediaMetadata?.artworkUri?.thumbnail(thumbSize)
    }
    val uiMedia = remember(mediaItem, duration) { mediaItem?.toUiMedia(duration) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (artworkUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(artworkUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.25f to Color.Transparent,
                            0.45f to Color.Transparent,
                            0.7f to colorPalette.background0,
                            1f to colorPalette.background0
                        )
                    )
            )

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    WindowInsets.systemBars
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                        .asPaddingValues()
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        BasicText(
                            text = metadata?.title?.toString().orEmpty(),
                            style = typography.l.bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        BasicText(
                            text = metadata?.artist?.toString().orEmpty(),
                            style = typography.s.semiBold.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(
                        painter = painterResource(
                            if (likedAt == null) R.drawable.heart_outline else R.drawable.heart
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.favoritesIcon),
                        modifier = Modifier
                            .clickable {
                                setLikedAt(
                                    if (likedAt == null) System.currentTimeMillis() else null
                                )
                            }
                            .size(24.dp)
                    )
                }

                if (uiMedia != null) {
                    Box(modifier = Modifier.padding(horizontal = 48.dp)) {
                        SeekBar(
                            binder = binder,
                            position = position,
                            media = uiMedia,
                            alwaysShowDuration = true,
                            style = PlayerPreferences.SeekBarStyle.Static,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(R.drawable.play_skip_back),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .clickable { player.forceSeekToPrevious() }
                            .size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(48.dp))

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable {
                                if (shouldBePlaying) player.pause()
                                else {
                                    if (player.playbackState == Player.STATE_IDLE) player.prepare()
                                    player.play()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isBuffering && shouldBePlaying) {
                            app.pulse.android.ui.components.themed.CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = colorPalette.text
                            )
                        } else {
                            Image(
                                painter = painterResource(
                                    if (shouldBePlaying) R.drawable.pause else R.drawable.play
                                ),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorPalette.text),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(48.dp))

                    Image(
                        painter = painterResource(R.drawable.play_skip_forward),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .clickable { player.forceSeekToNext() }
                            .size(32.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(162.dp))
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                NewLayoutVolumeSlider(
                    context = context,
                    colorPalette = colorPalette,
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .height(24.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp).padding(top = 28.dp, bottom = 48.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.sparkles),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .clickable(onClick = onLyricsClick)
                            .size(24.dp)
                    )

                    Image(
                        painter = painterResource(R.drawable.list),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .clickable(onClick = onQueueClick)
                            .size(24.dp)
                    )

                    Image(
                        painter = painterResource(R.drawable.ellipsis_horizontal),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorPalette.text),
                        modifier = Modifier
                            .clickable(onClick = onMenuLaunch)
                            .size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NewLayoutVolumeSlider(
    context: Context,
    colorPalette: app.pulse.core.ui.ColorPalette,
    modifier: Modifier = Modifier
) {
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var volume by remember {
        mutableFloatStateOf(
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        )
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
            }
        }
        context.registerReceiver(receiver, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
        onDispose { context.unregisterReceiver(receiver) }
    }

    var isDragging by remember { mutableStateOf(false) }
    val barHeight = 5.dp
    val fraction = volume / maxVolume

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.volume_muted),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorPalette.textDisabled),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight * 2)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newFraction = (offset.x / size.width).coerceIn(0f, 1f)
                        val newVol = (newFraction * maxVolume).roundToInt()
                        volume = newVol.toFloat()
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                    }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onHorizontalDrag = { _, delta ->
                            val newFraction = ((volume / maxVolume) + delta / size.width).coerceIn(0f, 1f)
                            val newVol = (newFraction * maxVolume).roundToInt()
                            volume = newVol.toFloat()
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    )
                }
                .drawBehind {
                    val barHeightPx = barHeight.toPx()
                    val trackY = (size.height - barHeightPx) / 2f
                    val fractionVal = fraction.coerceIn(0f, 1f)
                    val thumbX = fractionVal * size.width

                    drawRoundRect(
                        color = colorPalette.background2,
                        topLeft = Offset(0f, trackY),
                        size = Size(size.width, barHeightPx),
                        cornerRadius = CornerRadius(barHeightPx / 2f)
                    )

                    drawRoundRect(
                        color = colorPalette.accent,
                        topLeft = Offset(0f, trackY),
                        size = Size(thumbX, barHeightPx),
                        cornerRadius = CornerRadius(barHeightPx / 2f)
                    )

                    if (isDragging) {
                        val thumbWidth = 8.dp.toPx()
                        val thumbHeight = 16.dp.toPx()
                        drawRoundRect(
                            color = colorPalette.onAccent,
                            topLeft = Offset(thumbX - thumbWidth / 2f, (size.height - thumbHeight) / 2f),
                            size = Size(thumbWidth, thumbHeight),
                            cornerRadius = CornerRadius(thumbHeight / 2f)
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(R.drawable.volume_up),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colorPalette.textDisabled),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))
    }
}
