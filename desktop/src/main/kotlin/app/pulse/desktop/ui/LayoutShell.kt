package app.pulse.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.components.ContextPanel
import app.pulse.desktop.ui.components.MiniPlayer
import app.pulse.desktop.ui.components.TopNavBar
import app.pulse.core.data.models.Song
import app.pulse.providers.innertube.Innertube
import java.awt.Cursor

@Composable
fun LayoutShell(
    activeView: View,
    onNavigate: (View) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    homePage: Innertube.DiscoverPage?,
    onPageLoaded: (Result<Innertube.DiscoverPage>) -> Unit,
    onPlaySong: (Song) -> Unit,
    player: PlayerService,
    onOpenPlayer: () -> Unit,
    onToggleQueue: () -> Unit
) {
    val bg = Color(0xFF0a0a0a)

    // resizable sidebar widths
    var sidebarWidth by remember { mutableStateOf(460.dp) }
    var panelWidth by remember { mutableStateOf(460.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        // Main content — fills full window, touches all edges
        Column(modifier = Modifier.fillMaxSize()) {
            // Top navigation bar
            TopNavBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onNavigate = onNavigate
            )

            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                // Left sidebar
                Sidebar(
                    activeView = activeView,
                    onNavigate = onNavigate,
                    modifier = Modifier.width(sidebarWidth).fillMaxHeight()
                )

                // drag handle left panel
                ResizableHandle(
                    onDrag = { delta ->
                        sidebarWidth = (sidebarWidth + delta.dp).coerceIn(400.dp, 460.dp)
                    }
                )

                // Center content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    ContentView(
                        activeView = activeView,
                        searchQuery = searchQuery,
                        homePage = homePage,
                        onPageLoaded = onPageLoaded,
                        onPlaySong = onPlaySong
                    )
                }

                // drag handle right panel
                ResizableHandle(
                    onDrag = { delta ->
                        panelWidth = (panelWidth - delta.dp).coerceIn(400.dp, 460.dp)
                    }
                )

                // Right context panel
                ContextPanel(
                    player = player,
                    modifier = Modifier.width(panelWidth).fillMaxHeight()
                )
            }
        }

        // compact miniplayer
        val ps by player.state.collectAsState()
        if (ps.currentSong != null) {
            MiniPlayer(
                player = player,
                onClick = {},
                onOpenPlayer = onOpenPlayer,
                onToggleQueue = onToggleQueue,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 14.dp)
            )
        }
    }
}

// thin vertical drag handle for resizing sidebars
@Composable
private fun ResizableHandle(onDrag: (Float) -> Unit) {
    val s = LocalDensity.current.density
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val lineColor = if (isHovered) Color(0xFF666666) else Color(0xFF3a3a3a)
    Box(
        modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    onDrag(dragAmount / s)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // thin visible line
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(lineColor)
        )
        // grip thumb dot
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .background(lineColor, RoundedCornerShape(2.dp))
        )
    }
}
