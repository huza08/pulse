package app.pulse.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.components.MiniPlayer
import app.pulse.core.data.models.Song
import app.pulse.providers.innertube.Innertube

@Composable
fun LayoutShell(
    activeView: View,
    onNavigate: (View) -> Unit,
    homePage: Innertube.DiscoverPage?,
    onPageLoaded: (Result<Innertube.DiscoverPage>) -> Unit,
    onPlaySong: (Song) -> Unit,
    player: PlayerService,
    onOpenPlayer: () -> Unit,
    onToggleQueue: () -> Unit
) {
    val bg = Color(0xFF0a0a0a)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Left sidebar
            Sidebar(
                activeView = activeView,
                onNavigate = onNavigate
            )

            // Center content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                ContentView(
                    activeView = activeView,
                    homePage = homePage,
                    onPageLoaded = onPageLoaded,
                    onPlaySong = onPlaySong
                )
            }
        }

        // MiniPlayer pinned at bottom
        val ps by player.state.collectAsState()
        if (ps.currentSong != null) {
            MiniPlayer(
                player = player,
                onClick = {},
                onOpenPlayer = onOpenPlayer,
                onToggleQueue = onToggleQueue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 200.dp) // offset for sidebar width
            )
        }
    }
}
