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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.util.formatDuration

@Composable
fun MiniPlayer(
    player: PlayerService,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()
    val song = state.currentSong ?: return

    val bg = Color(0xFF141414)
    val surface = Color(0xFF1e1e1e)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(surface, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(start = 8.dp, end = 8.dp)
    ) {
        // Circular thumbnail
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(bg)
        ) {
            song.thumbnail?.let { thumb ->
                NetworkImage(
                    url = thumb.size(100),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = song.info?.name ?: "Untitled",
            color = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(180.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = formatDuration(state.currentPositionMs),
            color = dim,
            fontSize = 11.sp,
            modifier = Modifier.width(36.dp)
        )

        Spacer(Modifier.width(6.dp))

        val progress = if (state.durationMs > 0)
            (state.currentPositionMs.toFloat() / state.durationMs).coerceIn(0f, 1f) else 0f
        Slider(
            value = progress,
            onValueChange = { player.seek((it * state.durationMs).toLong()) },
            colors = SliderDefaults.colors(
                thumbColor = text,
                activeTrackColor = text,
                inactiveTrackColor = Color(0xFF2a2a2a)
            ),
            modifier = Modifier.width(120.dp).height(20.dp)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = formatDuration(state.durationMs),
            color = dim,
            fontSize = 11.sp,
            modifier = Modifier.width(36.dp)
        )

        Spacer(Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (state.isPlaying) player.pause()
                    else player.resume()
                }
        ) {
            Icon(
                painter = painterResource(
                    if (state.isPlaying) "/icons/pause.svg" else "/icons/play.svg"
                ),
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                tint = text,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Icon(
            painter = painterResource(
                if (state.volume <= 0f) "/icons/volume_muted.svg" else "/icons/volume_up.svg"
            ),
            contentDescription = "Volume",
            tint = dim,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Slider(
            value = state.volume,
            onValueChange = { player.setVolume(it) },
            colors = SliderDefaults.colors(
                thumbColor = dim,
                activeTrackColor = dim,
                inactiveTrackColor = Color(0xFF2a2a2a)
            ),
            modifier = Modifier.width(80.dp).height(20.dp)
        )
    }
}


