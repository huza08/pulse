package app.pulse.desktop.ui.screens.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
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
            val loopPainter = when (state.loopMode) {
                LoopMode.NONE -> painterResource("/icons/repeat.svg")
                LoopMode.ONE -> painterResource("/icons/repeat_on.svg")
                LoopMode.ALL -> painterResource("/icons/repeat.svg")
            }
            val loopAlpha = when (state.loopMode) {
                LoopMode.NONE -> 0.4f
                LoopMode.ONE, LoopMode.ALL -> 1f
            }
            MediaIconButton(
                painter = loopPainter,
                onClick = { player.cycleLoopMode() },
                contentDescription = "Repeat",
                tint = text.copy(alpha = loopAlpha),
                size = 18
            )

            Spacer(Modifier.width(16.dp))

            // Prev song
            MediaIconButton(
                painter = painterResource("/icons/play_skip_back.svg"),
                onClick = { if (state.hasPrevious) player.playPrevious() },
                contentDescription = "Previous",
                tint = text.copy(alpha = if (state.hasPrevious) 1f else 0.35f),
                size = 22
            )

            Spacer(Modifier.width(44.dp))

            // Play/Pause
            MediaIconButton(
                painter = if (state.isPlaying) painterResource("/icons/pause.svg") else painterResource("/icons/play.svg"),
                onClick = {
                    if (state.isPlaying) player.pause() else player.resume()
                },
                contentDescription = if (state.isPlaying) "Pause" else "Play",
                tint = text,
                size = 32,
                isMain = true
            )

            Spacer(Modifier.width(44.dp))

            // Next song
            MediaIconButton(
                painter = painterResource("/icons/play_skip_forward.svg"),
                onClick = { if (state.hasNext) player.playNext() },
                contentDescription = "Next",
                tint = text.copy(alpha = if (state.hasNext) 1f else 0.35f),
                size = 22
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))

        // Volume
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource("/icons/volume_up.svg"),
                contentDescription = "Volume",
                tint = dim,
                modifier = Modifier.size(14.dp)
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
private fun MediaIconButton(
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String,
    tint: Color,
    size: Int,
    isMain: Boolean = false
) {
    val btnSize = if (isMain) 56.dp else 40.dp
    val iconSize = (if (isMain) 28 else size).dp

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(btnSize)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSec = ms / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%d:%02d".format(min, sec)
}
