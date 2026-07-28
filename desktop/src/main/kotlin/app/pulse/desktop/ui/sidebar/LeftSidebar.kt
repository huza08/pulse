package app.pulse.desktop.ui.sidebar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.ui.View
import app.pulse.desktop.ui.constants.fonts.FontSizes
import app.pulse.desktop.ui.constants.sidebar.Left
import app.pulse.desktop.ui.constants.sizes.Sizes

private val TextColor = Color(0xFFf2f0eb)
private val DimColor = Color(0xFF686868)
private val ActiveBg = Color(0xFF2a2a2a)
private val GreenAccent = Color(0xFF1ed760)

@Composable
private fun Modifier.noRippleClick(onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }
    return clickable(interactionSource = source, indication = null, onClick = onClick)
}

@Composable
fun LeftSidebar(
    activeView: View,
    onNavigate: (View) -> Unit,
    isCollapsed: Boolean = false,
    isFadeOut: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    isWide: Boolean = true,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf(0) }

    val fixedOffset = Left.fixedOffset.dp
    val toggleTop = Left.headerTop.dp + Sizes.sidebarItemPadV.dp

    Box(modifier = modifier.fillMaxSize()) {
        ExpandedSidebar(
            activeView = activeView,
            onNavigate = onNavigate,
            onToggleCollapse = onToggleCollapse,
            filterTab = filterTab,
            onFilterTabChange = { filterTab = it },
            isWide = isWide,
            isCollapsed = isCollapsed,
            isFadeOut = isFadeOut
        )

        // toggle overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = Sizes.sidebarCollapsedPad.dp + fixedOffset, top = toggleTop),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(
                    if (isCollapsed || isFadeOut) "/icons/panel-left-open.svg"
                    else "/icons/panel-left-close.svg"
                ),
                contentDescription = if (isCollapsed || isFadeOut) "Expand sidebar" else "Collapse sidebar",
                tint = TextColor,
                modifier = Modifier
                    .size(Left.toggleIcon.dp)
                    .noRippleClick(onToggleCollapse)
            )
            AnimatedVisibility(
                visible = isFadeOut || isCollapsed,
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(Left.collapsedGap.dp))
                    Icon(
                        painter = painterResource("/icons/add.svg"),
                        contentDescription = "Create",
                        tint = DimColor,
                        modifier = Modifier
                            .size(Left.addIcon.dp)
                            .noRippleClick { /* todo */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedSidebar(
    activeView: View,
    onNavigate: (View) -> Unit,
    onToggleCollapse: () -> Unit,
    filterTab: Int,
    onFilterTabChange: (Int) -> Unit,
    isWide: Boolean = true,
    isCollapsed: Boolean = false,
    isFadeOut: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // scroll list to top when fade starts
    LaunchedEffect(isFadeOut) {
        if (isFadeOut) scrollState.scrollTo(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Sizes.sidebarCollapsedPad.dp)
            .padding(top = Left.headerTop.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val density = LocalDensity.current
        var sectionHeightPx by remember { mutableStateOf(0) }

        // spacer for overlay toggle + icon when collapsed
        val toggleAreaHeight by animateDpAsState(
            targetValue = if (isFadeOut || isCollapsed) Left.overlayH.dp
            else 0.dp,
            animationSpec = spring(dampingRatio = 1f, stiffness = 300f)
        )
        Spacer(Modifier.height(toggleAreaHeight))

        // section height animates same speed as spacer so total moves one direction
        val sectionHeight by animateDpAsState(
            targetValue = if (isFadeOut || isCollapsed) 0.dp
                         else with(density) { sectionHeightPx.toDp() },
            animationSpec = spring(dampingRatio = 1f, stiffness = 300f)
        )
        // custom layout measures full content height, reports animated height to parent
        val sectionHeightPxAnim = with(density) { sectionHeight.toPx() }.toInt()
        Layout(
            modifier = Modifier.fillMaxWidth().clipToBounds(),
            content = {
                Column(
                    modifier = Modifier
                        .padding(start = Left.expandedPad.dp, end = Left.expandedPad.dp)
                        .onSizeChanged { sectionHeightPx = it.height }
                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Left.headerBottom.dp)
                ) {
                    // offset spacer clears toggle icon
                    Spacer(Modifier.width(Left.toggleIcon.dp + Left.headerTextStart.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedVisibility(
                            visible = isWide,
                            enter = expandHorizontally(tween(300)) + fadeIn(tween(300)),
                            exit = shrinkHorizontally(tween(300)) + fadeOut(tween(300))
                        ) {
                            Text(
                                text = "Your Library",
                                color = TextColor,
                                fontSize = FontSizes.sidebarSection.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        HeaderIcon(painterResource("/icons/add.svg"), "Create", Left.addIcon.dp)
                    }
                }

                // filter pills
                if (isWide) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Left.sectionGap.dp)
                    ) {
                        FilterChip(
                            label = "Playlists",
                            selected = filterTab == 0,
                            onClick = { onFilterTabChange(0) }
                        )
                        Spacer(Modifier.width(Sizes.sidebarItemGap.dp))
                        FilterChip(
                            label = "Artists",
                            selected = filterTab == 1,
                            onClick = { onFilterTabChange(1) }
                        )
                    }

                    // search/sort row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Left.searchRowH.dp)
                    ) {
                        Icon(
                            painter = painterResource("/icons/search.svg"),
                            contentDescription = "Search",
                            tint = DimColor,
                            modifier = Modifier
                                .size(Left.searchIcon.dp)
                                .noRippleClick { /* todo */ }
                        )
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier.height(Left.listIcon.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Recents",
                                color = DimColor,
                                fontSize = FontSizes.sidebarSmall.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            painter = painterResource("/icons/list.svg"),
                            contentDescription = "Toggle view",
                            tint = DimColor,
                            modifier = Modifier
                                .size(Left.listIcon.dp)
                                .noRippleClick { /* todo */ }
                        )
                    }

                    Spacer(Modifier.height(Left.sectionGap.dp))
                }
            }
        }  // end content
    ) { measurables, constraints ->
        // measure at full height, report animated height to parent
        val placeable = measurables.first().measure(constraints)
        layout(placeable.width, sectionHeightPxAnim) {
            placeable.placeRelative(0, 0)
        }
    }  // end layout
        val listPadStart by animateDpAsState(
            targetValue = if (isCollapsed || isFadeOut) Sizes.sidebarCollapsedPad.dp else Left.expandedPad.dp,
            animationSpec = spring(dampingRatio = 1f, stiffness = 300f)
        )
        val listPadEnd by animateDpAsState(
            targetValue = if (isCollapsed || isFadeOut) Sizes.sidebarCollapsedPad.dp else Left.expandedPad.dp,
            animationSpec = spring(dampingRatio = 1f, stiffness = 300f)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = listPadStart, end = listPadEnd)
                .verticalScroll(scrollState)
        ) {
            when (filterTab) {
                0 -> PlaylistItems(activeView, onNavigate, isCollapsed, isFadeOut)
                1 -> ArtistItems(activeView, onNavigate, isCollapsed, isFadeOut)
            }
        }
    }
}

@Composable
private fun PlaylistItems(activeView: View, onNavigate: (View) -> Unit, isCollapsed: Boolean, isFadeOut: Boolean = false) {
    LibraryItem(
        icon = {
            Icon(
                painter = painterResource("/icons/heart.svg"),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(Left.heartIcon.dp)
            )
        },
        name = "Liked Songs",
        subtitle = "Playlist • 27 songs",
        isActive = false,
        isPinned = true,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Songs) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFFd4a373),
        name = "dolefulness",
        subtitle = "Playlist • huzaa",
        isActive = activeView == View.Playlists,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Playlists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF444444),
        name = "gugugaga",
        subtitle = "Playlist • huzaa",
        isActive = false,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Playlists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF555555),
        name = "My Playlist #5",
        subtitle = "Playlist • huzaa",
        isActive = false,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Playlists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF8a5a44),
        name = "a7x",
        subtitle = "Playlist • huzaa",
        isActive = false,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Playlists) }
    )
}

@Composable
private fun ArtistItems(activeView: View, onNavigate: (View) -> Unit, isCollapsed: Boolean, isFadeOut: Boolean = false) {
    LibraryItem(
        iconPlaceholder = Color(0xFF333333),
        isCircular = true,
        name = "Decalius",
        subtitle = "Artist",
        isActive = false,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Artists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF444444),
        isCircular = true,
        name = "Sadness",
        subtitle = "Artist",
        isActive = false,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Artists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF555555),
        isCircular = true,
        name = "Bring Me The Horizon",
        subtitle = "Artist",
        isActive = false,
        isCollapsed = isCollapsed,
        isFadeOut = isFadeOut,
        onClick = { onNavigate(View.Artists) }
    )
}

@Composable
private fun HeaderIcon(painter: Painter, desc: String, size: Dp = Left.iconMd.dp) {        Icon(
            painter = painter,
            contentDescription = desc,
            tint = DimColor,
            modifier = Modifier
                .size(size)
                .noRippleClick { /* todo */ }
        )
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Sizes.radiusPill.dp))
            .background(if (selected) Color(0xFF2a2a2a) else Color(0xFF141414))
            .noRippleClick(onClick)
            .padding(horizontal = Left.filterPadH.dp, vertical = Sizes.queueItemPadV.dp)
    ) {
        Text(
            text = label,
            color = if (selected) TextColor else DimColor,
            fontSize = FontSizes.sidebarChip.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun LibraryItem(
    icon: @Composable (() -> Unit)? = null,
    iconPlaceholder: Color = Color(0xFF444444),
    isCircular: Boolean = false,
    name: String,
    subtitle: String,
    isActive: Boolean,
    isPinned: Boolean = false,
    isCollapsed: Boolean = false,
    isFadeOut: Boolean = false,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) ActiveBg else Color.Transparent
    val textAlpha by animateFloatAsState(
        targetValue = if (isFadeOut || isCollapsed) 0f else 1f,
        animationSpec = spring(dampingRatio = 1f, stiffness = 300f)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Sizes.radiusSm.dp))
            .background(bgColor)
            .noRippleClick(onClick)
            .padding(vertical = Sizes.sidebarItemPadV.dp)
    ) {
        Box(
            modifier = Modifier
                .size(Left.thumbSize.dp)
                .clip(if (isCircular) CircleShape else RoundedCornerShape(Sizes.radiusSm.dp))
                .background(iconPlaceholder),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            }
        }

        // text column fades when collapsed
        Spacer(Modifier.width(Sizes.sidebarItemPadH.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .alpha(textAlpha)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    color = if (isActive) GreenAccent else TextColor,
                    fontSize = FontSizes.sidebarItem.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isPinned) {
                    Spacer(Modifier.width(Sizes.sidebarItemGap.dp))
                    Icon(
                        painter = painterResource("/icons/heart.svg"),
                        contentDescription = null,
                        tint = GreenAccent,
                        modifier = Modifier.size(Left.pinnedIcon.dp)
                    )
                }
            }
            Text(
                text = subtitle,
                color = DimColor,
                fontSize = FontSizes.sidebarSub.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
