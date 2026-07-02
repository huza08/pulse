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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.LoopMode
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

            var isDragging by remember { mutableStateOf(false) }
            var dragFraction by remember { mutableFloatStateOf(0f) }

            val displayFraction = if (isDragging) dragFraction
            else if (state.durationMs > 0) (state.currentPositionMs.toFloat() / state.durationMs).coerceIn(0f, 1f)
            else 0f

            Slider(
                value = displayFraction,
                onValueChange = { fraction ->
                    isDragging = true
                    dragFraction = fraction
                },
                onValueChangeFinished = {
                    isDragging = false
                    player.seek((dragFraction * state.durationMs).toLong())
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

            // Loop mode
            val loopIcon = when (state.loopMode) {
                LoopMode.NONE -> "🔁"
                LoopMode.ONE -> "🔂"
                LoopMode.ALL -> "🔁 A"
            }
            val loopAlpha = when (state.loopMode) {
                LoopMode.NONE -> 0.4f
                LoopMode.ONE, LoopMode.ALL -> 1f
            }
            ControlButton(
                text = loopIcon,
                onClick = { player.cycleLoopMode() },
                size = 16,
                alpha = loopAlpha
            )

            Spacer(Modifier.width(16.dp))

            // Prev song
            ControlButton(
                text = "⏮",
                onClick = { if (state.hasPrevious) player.playPrevious() },
                size = 20
            )

            Spacer(Modifier.width(16.dp))

            // Rewind 10s
            ControlButton(text = "⏪", onClick = { player.skipBackward(10) }, size = 24)

            Spacer(Modifier.width(20.dp))

            // Play/Pause
            ControlButton(
                text = if (state.isPlaying) "⏸" else "▶",
                onClick = {
                    if (state.isPlaying) player.pause() else player.resume()
                },
                size = 32,
                isMain = true
            )

            Spacer(Modifier.width(20.dp))

            // Forward 10s
            ControlButton(text = "⏩", onClick = { player.skipForward(10) }, size = 24)

            Spacer(Modifier.width(16.dp))

            // Next song
            ControlButton(
                text = "⏭",
                onClick = { if (state.hasNext) player.playNext() },
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
    isMain: Boolean = false,
    alpha: Float = 1f
) {
    val btnSize = if (isMain) 56.dp else 40.dp
    val fontSize = if (isMain) 20.sp else 16.sp

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(btnSize)
    ) {
        Text(
            text = text,
            color = Color(0xFFf2f0eb).copy(alpha = alpha),
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
