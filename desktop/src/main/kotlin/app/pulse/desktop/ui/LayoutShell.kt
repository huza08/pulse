package app.pulse.desktop.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.sidebar.RightPanelState
import app.pulse.desktop.ui.sidebar.RightSidebar
import app.pulse.desktop.ui.sidebar.SidebarLeft
import app.pulse.desktop.ui.constants.sizes.RightPanel
import app.pulse.desktop.ui.constants.sizes.Sizes
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
    player: PlayerService
) {
    val bg = Color(0xFF0a0a0a)

    // right panel width — animated triggers smooth center content re-layout
    var targetPanelWidth by remember { mutableStateOf(RightPanel.targetWidth.dp) }
    var panelState by remember { mutableStateOf(RightPanelState.EXPANDED) }
    var isPeeking by remember { mutableStateOf(false) }

    val hideAnim = spring<Dp>(dampingRatio = 1f, stiffness = 300f)

    val panelWidth by animateDpAsState(
        targetValue = when {
            panelState == RightPanelState.EXPANDED -> targetPanelWidth
            isPeeking -> RightPanel.intermediateWidth.dp
            else -> RightPanel.collapsedWidth.dp
        },
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
                    .padding(RightPanel.padding.dp),
                horizontalArrangement = Arrangement.spacedBy(RightPanel.padding.dp)
            ) {
                // left sidebar
                SidebarLeft(
                    onNavigate = onNavigate,
                    modifier = Modifier.fillMaxHeight()
                )

                // Center content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .requiredWidthIn(min = Sizes.centerMinWidth.dp)
                        .clip(RoundedCornerShape(Sizes.radiusMd.dp))
                        .background(Color(0xFF121212), RoundedCornerShape(Sizes.radiusMd.dp))
                        .border(1.dp, Color(0xFF2a2a2a), RoundedCornerShape(Sizes.radiusMd.dp))
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
                ResizableSidebar(
                    width = panelWidth,
                    onWidthChange = { targetPanelWidth = it },
                    minWidth = if (panelState == RightPanelState.EXPANDED) RightPanel.minWidth.dp else RightPanel.collapsedWidth.dp,
                    maxWidth = RightPanel.maxWidth.dp
                ) {
                    RightSidebar(
                        player = player,
                        panelState = panelState,
                        onCycleState = {
                            if (panelState == RightPanelState.COLLAPSED) {
                                targetPanelWidth = RightPanel.minWidth.dp
                            }
                            panelState = if (panelState == RightPanelState.EXPANDED)
                                RightPanelState.COLLAPSED
                            else
                                RightPanelState.EXPANDED
                        },
                        onPeekChange = { peeking -> isPeeking = peeking },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// right panel resizable wrapper — handle sits on the panel's start edge
@Composable
private fun ResizableSidebar(
    width: Dp,
    onWidthChange: (Dp) -> Unit,
    minWidth: Dp,
    maxWidth: Dp,
    content: @Composable BoxScope.() -> Unit
) {
    val handleOffsetDp = (RightPanel.padding / 2 + 8).dp

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
    ) {
        // card surface
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Sizes.radiusMd.dp))
                .background(Color(0xFF121212), RoundedCornerShape(Sizes.radiusMd.dp))
                .border(1.dp, Color(0xFF2a2a2a), RoundedCornerShape(Sizes.radiusMd.dp))
        ) {
            content()
        }
        // resize handle
        ResizableHandle(
            modifier = Modifier.align(Alignment.CenterStart).offset(x = -handleOffsetDp),
            onDrag = { delta ->
                val newWidth = width - (delta).dp
                onWidthChange(newWidth.coerceIn(minWidth, maxWidth))
            }
        )
    }
}

// drag hint
@Composable
internal fun ResizableHandle(modifier: Modifier = Modifier, onDrag: (Float) -> Unit) {
    val s = LocalDensity.current.density
    val currentOnDrag by rememberUpdatedState(onDrag)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val hintColor = if (isHovered) Color(0xFF555555) else Color.Transparent

    Box(
        modifier = modifier
            .width(Sizes.resizerW.dp)
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
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight(Sizes.resizerHintH)
                .background(hintColor, RoundedCornerShape(1.dp))
        )
    }
}
