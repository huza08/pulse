package app.pulse.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
                // handle
                ResizableHandle(
                    modifier = Modifier.offset(x = (-8).dp),
                    onDrag = { delta ->
                        sidebarWidth = (sidebarWidth + delta.dp).coerceIn(200.dp, 460.dp)
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

                Box(
                    modifier = Modifier
                        .width(panelWidth)
                        .fillMaxHeight()
                ) {
                    ContextPanel(
                        player = player,
                        modifier = Modifier.fillMaxSize()
                    )
                    ResizableHandle(
                        modifier = Modifier.align(Alignment.CenterStart).offset(x = (-8).dp),
                        onDrag = { delta ->
                            panelWidth = (panelWidth - delta.dp).coerceIn(250.dp, 460.dp)
                        }
                    )
                }
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

// drag handle
@Composable
private fun ResizableHandle(modifier: Modifier = Modifier, onDrag: (Float) -> Unit) {
    val s = LocalDensity.current.density
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val pillBg = if (isHovered) Color(0xFF2a2a2a) else Color(0xFF1a1a1a)
    val dotColor = if (isHovered) Color(0xFF666666) else Color(0xFF555555)
    Box(
        modifier = modifier
            .width(16.dp)
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
        // pill dots
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .width(16.dp)
                .height(42.dp)
                .border(1.dp, Color(0xFF3a3a3a), RoundedCornerShape(67.dp))
                .background(pillBg, RoundedCornerShape(67.dp))
                .padding(vertical = 4.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(8.dp)
                        .background(dotColor, RoundedCornerShape(67.dp))
                )
            }
        }
    }
}
