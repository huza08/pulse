package app.pulse.desktop.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.LoopMode
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.util.formatDuration

private val pillShape = RoundedCornerShape(55.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniPlayer(
    player: PlayerService,
    onClick: () -> Unit,
    onOpenPlayer: () -> Unit = onClick,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()
    val song = state.currentSong ?: return

    val bg = Color(0xFF1e1e1e)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)
    val accent = Color(0xFFf2f0eb)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(12.dp, pillShape)
            .background(bg, pillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp)
    ) {
        // left track info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF141414))
            ) {
                song.thumbnail?.let { thumb ->
                    NetworkImage(url = thumb.size(200), modifier = Modifier.size(80.dp))
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = song.info?.name ?: "Untitled",
                    color = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                song.authors?.firstOrNull()?.let { author ->
                    Text(
                        text = author.name ?: "",
                        color = dim,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // center controls + seekbar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-8).dp, Alignment.CenterVertically),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Icon(
                    painter = painterResource("/icons/shuffle.svg"),
                    contentDescription = "Shuffle",
                    tint = dim,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(16.dp))

                IconButton16(
                    painter = painterResource("/icons/play_skip_back.svg"),
                    desc = "Previous",
                    tint = text,
                    size = 32.dp,
                    onClick = { if (state.hasPrevious) player.playPrevious() }
                )
                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (state.isPlaying) player.pause() else player.resume()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (state.isPlaying) "/icons/pause.svg" else "/icons/play.svg"
                        ),
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        tint = Color(0xFFFFFFFF),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))

                IconButton16(
                    painter = painterResource("/icons/play_skip_forward.svg"),
                    desc = "Next",
                    tint = text,
                    size = 32.dp,
                    onClick = { if (state.hasNext) player.playNext() }
                )
                Spacer(Modifier.width(16.dp))

                val repeatTint = when (state.loopMode) {
                    LoopMode.NONE -> dim.copy(alpha = 0.4f)
                    LoopMode.ONE, LoopMode.ALL -> accent
                }
                val repeatIcon = when (state.loopMode) {
                    LoopMode.ONE -> "/icons/repeat_on.svg"
                    else -> "/icons/repeat.svg"
                }
                Icon(
                    painter = painterResource(repeatIcon),
                    contentDescription = "Repeat",
                    tint = repeatTint,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { player.cycleLoopMode() }
                        )
                )
            }

            // seekbar row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(560.dp)
            ) {
                Text(
                    text = formatDuration(state.currentPositionMs),
                    color = dim,
                    fontSize = 11.sp,
                    modifier = Modifier.width(34.dp)
                )
                val seekInteractionSource = remember { MutableInteractionSource() }
                val isSeekHovered by seekInteractionSource.collectIsHoveredAsState()
                val isSeekPressed by seekInteractionSource.collectIsPressedAsState()
                val isSeekDragged by seekInteractionSource.collectIsDraggedAsState()
                val isSeekActive = isSeekHovered || isSeekPressed || isSeekDragged
                val seekHeight by animateDpAsState(if (isSeekActive) 12.dp else 6.dp)
                val seekWidthScale by animateFloatAsState(if (isSeekActive) 1.02f else 1f)

                val progress = if (state.durationMs > 0)
                    (state.currentPositionMs.toFloat() / state.durationMs).coerceIn(0f, 1f) else 0f
                Slider(
                    value = progress,
                    onValueChange = { player.seek((it * state.durationMs).toLong()) },
                    interactionSource = seekInteractionSource,
                    track = { sliderState ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(seekWidthScale)
                                .height(seekHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            // Inactive track
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(seekHeight)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2a2a2a))
                            )
                            // Active track
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(sliderState.value)
                                    .height(seekHeight)
                                    .clip(CircleShape)
                                    .background(accent)
                                    .align(Alignment.CenterStart)
                            )
                        }
                    },
                    thumb = {},
                    modifier = Modifier.weight(1f).height(32.dp)
                )
                Text(
                    text = formatDuration(state.durationMs),
                    color = dim,
                    fontSize = 11.sp,
                    modifier = Modifier.width(34.dp)
                )
            }
        }

        // right volume
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {

            Spacer(Modifier.width(14.dp))
            Icon(
                painter = painterResource("/icons/lyrics.svg"),
                contentDescription = "Lyrics",
                tint = dim,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(14.dp))
            Icon(
                painter = painterResource("/icons/list.svg"),
                contentDescription = "Queue",
                tint = dim,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(14.dp))
            Icon(
                painter = painterResource(
                    if (state.volume <= 0f) "/icons/volume_muted.svg" else "/icons/volume_up.svg"
                ),
                contentDescription = "Volume",
                tint = dim,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.width(12.dp))

            val volInteractionSource = remember { MutableInteractionSource() }
            val isVolHovered by volInteractionSource.collectIsHoveredAsState()
            val isVolPressed by volInteractionSource.collectIsPressedAsState()
            val isVolDragged by volInteractionSource.collectIsDraggedAsState()
            val isVolActive = isVolHovered || isVolPressed || isVolDragged
            val volHeight by animateDpAsState(if (isVolActive) 12.dp else 6.dp)

            Slider(
                value = state.volume,
                onValueChange = { player.setVolume(it) },
                interactionSource = volInteractionSource,
                track = { sliderState ->
                    Box(
                        modifier = Modifier.fillMaxWidth().height(volHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        // inactive
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(volHeight)
                                .clip(CircleShape)
                                .background(Color(0xFF2a2a2a))
                        )
                        // active
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(sliderState.value)
                                .height(volHeight)
                                .clip(CircleShape)
                                .background(dim)
                                .align(Alignment.CenterStart)
                        )
                    }
                },
                thumb = {},
                modifier = Modifier.width(96.dp).height(32.dp)
            )
            Spacer(Modifier.width(14.dp))
            Icon(
                painter = painterResource("/icons/expand.svg"),
                contentDescription = "Fullscreen",
                tint = text,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onOpenPlayer
                    )
            )
        }
    }
}

@Composable
private fun IconButton16(
    painter: Painter,
    desc: String,
    tint: Color,
    size: Dp = 20.dp,
    onClick: () -> Unit
) {
    Icon(
        painter = painter,
        contentDescription = desc,
        tint = tint,
        modifier = Modifier
            .size(size)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    )
}
