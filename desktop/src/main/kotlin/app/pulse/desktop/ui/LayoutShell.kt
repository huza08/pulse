package app.pulse.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.components.MiniPlayer

@Composable
fun LayoutShell(
    player: PlayerService,
    onOpenPlayer: () -> Unit,
    onToggleQueue: () -> Unit,
    content: @Composable () -> Unit
) {
    val bg = Color(0xFF0a0a0a)
    val sidebarBg = Color(0xFF0d0d0d)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Left sidebar — placeholder for Phase 1
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .fillMaxHeight()
                    .background(sidebarBg)
            ) {
                // Minimal placeholder: Pulse logo top, nav icons
                Text(
                    text = "P",
                    color = Color(0xFFf2f0eb),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
                )
            }

            // Center content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                content()
            }

        }

        // miniplayer pinned at bottom
        val ps by player.state.collectAsState()
        if (ps.currentSong != null) {
            MiniPlayer(
                player = player,
                onClick = {},
                onOpenPlayer = onOpenPlayer,
                onToggleQueue = onToggleQueue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp) // offset for sidebar width
            )
        }
    }
}
