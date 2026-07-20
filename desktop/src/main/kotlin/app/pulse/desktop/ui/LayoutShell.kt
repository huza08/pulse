package app.pulse.desktop.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.sidebar.LeftSidebar
import app.pulse.desktop.ui.sidebar.RightSidebar
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

    // sidebar widths — animated triggers smooth center content re-layout
    var targetSidebarWidth by remember { mutableStateOf(340.dp) }
    var targetPanelWidth by remember { mutableStateOf(460.dp) }
    var showRightPanel by remember { mutableStateOf(true) }
    var sidebarCollapsed by remember { mutableStateOf(false) }

    val hideAnim = spring<Dp>(dampingRatio = 0.8f, stiffness = 500f)

    // collapse/expand via drag:
    if (targetSidebarWidth <= 80.dp) {
        sidebarCollapsed = true
    } else if (targetSidebarWidth >= 120.dp) {
        sidebarCollapsed = false
    }

    val sidebarWidth by animateDpAsState(
        targetValue = if (sidebarCollapsed) 80.dp else targetSidebarWidth,
        animationSpec = hideAnim
    )
    val panelWidth by animateDpAsState(
        targetValue = if (showRightPanel) targetPanelWidth else 0.dp,
        animationSpec = hideAnim
    )

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

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left sidebar
                    ResizableSidebar(
                        width = sidebarWidth,
                        onWidthChange = { targetSidebarWidth = it },
                        minWidth = 80.dp,
                        maxWidth = 460.dp,
                        handleIsStart = false
                    ) {
                        LeftSidebar(
                            activeView = activeView,
                            onNavigate = onNavigate,
                            isCollapsed = sidebarCollapsed,
                            onToggleCollapse = {
                                sidebarCollapsed = !sidebarCollapsed
                                if (sidebarCollapsed) {
                                    targetSidebarWidth = 80.dp
                                } else {
                                    targetSidebarWidth = 340.dp
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                // Center content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF121212), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF2a2a2a), RoundedCornerShape(8.dp))
                ) {
                    ContentView(
                        activeView = activeView,
                        searchQuery = searchQuery,
                        homePage = homePage,
                        onPageLoaded = onPageLoaded,
                        onPlaySong = onPlaySong
                    )
                }

                // Right panel — animated width triggers smooth center reflow
                if (showRightPanel || panelWidth > 0.dp) {
                    ResizableSidebar(
                        width = panelWidth,
                        onWidthChange = { targetPanelWidth = it },
                        minWidth = 250.dp,
                        maxWidth = 460.dp,
                        handleIsStart = true
                    ) {
                        RightSidebar(
                            player = player,
                            onHidePanel = { showRightPanel = false },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // re-show buttons
        if (!showRightPanel && panelWidth <= 1.dp) {
            ReShowButton(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                icon = "/icons/chevron_back.svg",
                desc = "Show panel",
                onClick = { showRightPanel = true }
            )
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

// re-show pill button — alignment/padding set by caller (has BoxScope)
@Composable
private fun ReShowButton(
    modifier: Modifier = Modifier,
    icon: String,
    desc: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1c1c1c))
            .border(1.dp, Color(0xFF2a2a2a), RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = desc,
            tint = Color(0xFFf2f0eb),
            modifier = Modifier.size(18.dp)
        )
    }
}

// resizable sidebar wrapper
@Composable
private fun ResizableSidebar(
    width: Dp,
    onWidthChange: (Dp) -> Unit,
    minWidth: Dp,
    maxWidth: Dp,
    handleIsStart: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val (align, handleOffset, dragSign) = if (handleIsStart) Triple(
        Alignment.CenterStart,
        (-8).dp,
        -1f
    ) else Triple(
        Alignment.CenterEnd,
        8.dp,
        1f
    )

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
    ) {
        // card surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF2a2a2a), RoundedCornerShape(8.dp))
        ) {
            content()
        }
        // resize handle
        ResizableHandle(
            modifier = Modifier.align(align).offset(x = handleOffset),
            onDrag = { delta ->
                val newWidth = width + (delta * dragSign).dp
                onWidthChange(newWidth.coerceIn(minWidth, maxWidth))
            }
        )
    }
}

// drag handle
@Composable
private fun ResizableHandle(modifier: Modifier = Modifier, onDrag: (Float) -> Unit) {
    val s = LocalDensity.current.density
    val currentOnDrag by rememberUpdatedState(onDrag)
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
                    currentOnDrag(dragAmount / s)
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
