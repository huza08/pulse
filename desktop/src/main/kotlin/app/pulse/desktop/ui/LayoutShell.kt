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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
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
import androidx.compose.ui.unit.dp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.sidebar.LeftSidebar
import app.pulse.desktop.ui.sidebar.RightPanelState
import app.pulse.desktop.ui.sidebar.RightSidebar
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

    // sidebar widths — animated triggers smooth center content re-layout
    var targetSidebarWidth by remember { mutableStateOf(Sizes.sidebarTargetWidth.dp) }
    var targetPanelWidth by remember { mutableStateOf(Sizes.panelTargetWidth.dp) }
    var panelState by remember { mutableStateOf(RightPanelState.EXPANDED) }
    var isPeeking by remember { mutableStateOf(false) }
    var sidebarCollapsed by remember { mutableStateOf(false) }

    val hideAnim = spring<Dp>(dampingRatio = 1f, stiffness = 300f)

    // collapse/expand via drag:
    if (targetSidebarWidth <= Sizes.sidebarCollapsedDrag.dp) {
        sidebarCollapsed = true
    } else if (targetSidebarWidth >= Sizes.sidebarUncollapseThreshold.dp) {
        sidebarCollapsed = false
    }

    val sidebarWidth by animateDpAsState(
        targetValue = if (sidebarCollapsed) Sizes.sidebarCollapsedDrag.dp else targetSidebarWidth,
        animationSpec = hideAnim
    )
    val panelWidth by animateDpAsState(
        targetValue = when {
            panelState == RightPanelState.EXPANDED -> targetPanelWidth
            isPeeking -> Sizes.rightIntermediateWidth.dp
            else -> Sizes.rightCollapsedWidth.dp
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
                    .padding(Sizes.rightPanelPadding.dp),
                horizontalArrangement = Arrangement.spacedBy(Sizes.rightPanelPadding.dp)
            ) {
                // Left sidebar
                    ResizableSidebar(
                        width = sidebarWidth,
                        onWidthChange = { targetSidebarWidth = it },
                        minWidth = Sizes.sidebarMinWidth.dp,
                        maxWidth = Sizes.sidebarMaxWidth.dp,
                        handleIsStart = false
                    ) {
                        val isWide = sidebarWidth > Sizes.sidebarWideThreshold.dp
                        LeftSidebar(
                            activeView = activeView,
                            onNavigate = onNavigate,
                            isCollapsed = sidebarCollapsed,
                            onToggleCollapse = {
                                sidebarCollapsed = !sidebarCollapsed
                                if (sidebarCollapsed) {
                                    targetSidebarWidth = Sizes.sidebarCollapsedDrag.dp
                                } else {
                                    targetSidebarWidth = Sizes.sidebarRestoreWidth.dp
                                }
                            },
                            isWide = isWide,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

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
                    minWidth = if (panelState == RightPanelState.EXPANDED) Sizes.panelMinWidth.dp else Sizes.rightCollapsedWidth.dp,
                    maxWidth = Sizes.panelMaxWidth.dp,
                    handleIsStart = true
                ) {
                    RightSidebar(
                        player = player,
                        panelState = panelState,
                        onCycleState = {
                            if (panelState == RightPanelState.COLLAPSED) {
                                targetPanelWidth = Sizes.panelMinWidth.dp
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
    val handleOffsetDp = (Sizes.rightPanelPadding / 2 + 8).dp
    val (align, handleOffset, dragSign) = if (handleIsStart) Triple(
        Alignment.CenterStart,
        -handleOffsetDp,
        -1f
    ) else Triple(
        Alignment.CenterEnd,
        handleOffsetDp,
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
                .clip(RoundedCornerShape(Sizes.radiusMd.dp))
                .background(Color(0xFF121212), RoundedCornerShape(Sizes.radiusMd.dp))
                .border(1.dp, Color(0xFF2a2a2a), RoundedCornerShape(Sizes.radiusMd.dp))
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

// drag hint
@Composable
private fun ResizableHandle(modifier: Modifier = Modifier, onDrag: (Float) -> Unit) {
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
