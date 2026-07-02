package app.pulse.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.LoopMode
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.util.formatDuration

private val pillShape = RoundedCornerShape(32.dp)

@Composable
fun MiniPlayer(
    player: PlayerService,
    onClick: () -> Unit,
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
            .height(64.dp)
            .shadow(12.dp, pillShape, ambientColor = Color.Black.copy(alpha = 0.4f), spotColor = Color.Black.copy(alpha = 0.4f))
            .background(bg, pillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(start = 6.dp, end = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF141414))
        ) {
            song.thumbnail?.let { thumb ->
                NetworkImage(url = thumb.size(100), modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = song.info?.name ?: "Untitled",
            color = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(130.dp)
        )
        Spacer(Modifier.width(6.dp))

        Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource("/icons/shuffle.svg"),
            contentDescription = "Shuffle",
            tint = dim,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))

        IconButton14(
            painter = painterResource("/icons/play_skip_back.svg"),
            desc = "Previous",
            tint = text,
            onClick = { if (state.hasPrevious) player.playPrevious() }
        )
        Spacer(Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(text)   // white circle
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
                tint = Color(0xFF0a0a0a),   // dark icon on white circle
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(4.dp))

        IconButton14(
            painter = painterResource("/icons/play_skip_forward.svg"),
            desc = "Next",
            tint = text,
            onClick = { if (state.hasNext) player.playNext() }
        )
        Spacer(Modifier.width(4.dp))

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
                .size(14.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { player.cycleLoopMode() }
                )
        )
        Spacer(Modifier.width(8.dp))

        // ── Progress: time + slider + time ────────────────────────────────
        Text(formatDuration(state.currentPositionMs), color = dim, fontSize = 10.sp, modifier = Modifier.width(30.dp))
        val progress = if (state.durationMs > 0)
            (state.currentPositionMs.toFloat() / state.durationMs).coerceIn(0f, 1f) else 0f
        Slider(
            value = progress,
            onValueChange = { player.seek((it * state.durationMs).toLong()) },
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color(0xFF2a2a2a)
            ),
            modifier = Modifier.width(100.dp).height(16.dp)
        )
        Text(formatDuration(state.durationMs), color = dim, fontSize = 10.sp, modifier = Modifier.width(30.dp))
        Spacer(Modifier.width(8.dp))

        Spacer(Modifier.weight(1f))

        // ── Utility: volume ───────────────────────────────────────────────
        Icon(
            painter = painterResource(
                if (state.volume <= 0f) "/icons/volume_muted.svg" else "/icons/volume_up.svg"
            ),
            contentDescription = "Volume",
            tint = dim,
            modifier = Modifier.size(14.dp)
        )
        Slider(
            value = state.volume,
            onValueChange = { player.setVolume(it) },
            colors = SliderDefaults.colors(
                thumbColor = dim,
                activeTrackColor = dim,
                inactiveTrackColor = Color(0xFF2a2a2a)
            ),
            modifier = Modifier.width(60.dp).height(16.dp)
        )
        Spacer(Modifier.width(4.dp))

        // ── Utility: lyrics ───────────────────────────────────────────────
        Icon(
            painter = painterResource("/icons/lyrics.svg"),
            contentDescription = "Lyrics",
            tint = dim,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))

        // ── Utility: queue ────────────────────────────────────────────────
        Icon(
            painter = painterResource("/icons/list.svg"),
            contentDescription = "Queue",
            tint = dim,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun IconButton14(
    painter: Painter,
    desc: String,
    tint: Color,
    onClick: () -> Unit,
    size: Dp = 16.dp
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
