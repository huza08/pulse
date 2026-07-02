package app.pulse.desktop.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService

@Composable
fun Controls(
    player: PlayerService,
    accent: Color,
    text: Color,
    dim: Color,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Seek bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatDuration(state.currentPositionMs),
                color = dim,
                fontSize = 11.sp,
                modifier = Modifier.width(48.dp)
            )

            Slider(
                value = if (state.durationMs > 0) {
                    (state.currentPositionMs.toFloat() / state.durationMs).coerceIn(0f, 1f)
                } else 0f,
                onValueChange = { fraction ->
                    player.seek((fraction * state.durationMs).toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = accent,
                    activeTrackColor = accent,
                    inactiveTrackColor = Color(0xFF2a2a2a)
                ),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = formatDuration(state.durationMs),
                color = dim,
                fontSize = 11.sp,
                modifier = Modifier.width(48.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Playback buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.weight(1f))

            // Prev
            ControlButton(
                text = "⏮",
                onClick = { player.stop() },
                size = 20
            )

            Spacer(Modifier.width(24.dp))

            // Play/Pause
            ControlButton(
                text = if (state.isPlaying) "⏸" else "▶",
                onClick = {
                    if (state.isPlaying) player.pause() else player.resume()
                },
                size = 32,
                isMain = true
            )

            Spacer(Modifier.width(24.dp))

            // Next
            ControlButton(
                text = "⏭",
                onClick = { player.stop() },
                size = 20
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Volume
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🔊",
                color = dim,
                fontSize = 11.sp
            )
            Spacer(Modifier.width(8.dp))

            var volume by remember { mutableFloatStateOf(state.volume) }
            Slider(
                value = volume,
                onValueChange = {
                    volume = it
                    player.setVolume(it)
                },
                colors = SliderDefaults.colors(
                    thumbColor = dim,
                    activeTrackColor = dim,
                    inactiveTrackColor = Color(0xFF2a2a2a)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ControlButton(
    text: String,
    onClick: () -> Unit,
    size: Int,
    isMain: Boolean = false
) {
    val btnSize = if (isMain) 56.dp else 40.dp
    val fontSize = if (isMain) 20.sp else 16.sp
    val bg = if (isMain) Color(0xFF2a2a2a) else Color.Transparent

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(btnSize)
    ) {
        Text(
            text = text,
            color = Color(0xFFf2f0eb),
            fontSize = fontSize
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
