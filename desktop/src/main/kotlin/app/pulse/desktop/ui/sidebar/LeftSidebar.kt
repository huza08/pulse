package app.pulse.desktop.ui.sidebar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.ui.View
import app.pulse.desktop.ui.components.FontSizes
import app.pulse.desktop.ui.components.Sizes
private val TextColor = Color(0xFFf2f0eb)
private val DimColor = Color(0xFF686868)
private val ActiveBg = Color(0xFF2a2a2a)
private val GreenAccent = Color(0xFF1ed760)

@Composable
fun LeftSidebar(
    activeView: View,
    onNavigate: (View) -> Unit,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    isWide: Boolean = true,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf(0) }

    if (isCollapsed) {
        CollapsedSidebar(
            onToggleCollapse = onToggleCollapse,
            modifier = modifier.requiredWidth(Sizes.sidebarCollapsedWidth.dp)
        )
    } else {
    ExpandedSidebar(
        activeView = activeView,
        onNavigate = onNavigate,
        onToggleCollapse = onToggleCollapse,
        filterTab = filterTab,
        onFilterTabChange = { filterTab = it },
        isWide = isWide,
        modifier = modifier
    )
    }
}

@Composable
private fun CollapsedSidebar(
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = Sizes.rightPanelPadding.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // header
        Icon(
            painter = painterResource("/icons/chevron_forward.svg"),
            contentDescription = "Expand sidebar",
            tint = TextColor,
            modifier = Modifier
                .size(Sizes.sidebarIconLg.dp)
                .padding(Sizes.sidebarItemGap.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggleCollapse
                )
        )
        Spacer(Modifier.height(Sizes.sidebarItemGap.dp))
        Icon(
            painter = painterResource("/icons/add.svg"),
            contentDescription = "Create",
            tint = DimColor,
            modifier = Modifier
                .size(Sizes.sidebarIconLg.dp)
                .padding(Sizes.sidebarIconPad.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { /* todo */ }
                )
        )

        Spacer(Modifier.height(Sizes.rightPanelPadding.dp))

        // icon-only items
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Sizes.sidebarItemGap.dp)
        ) {
            // Liked Songs
            Box(
                modifier = Modifier
                    .size(Sizes.sidebarThumbSize.dp)
                    .clip(RoundedCornerShape(Sizes.radiusSm.dp))
                    .background(Color(0xFFb02897)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource("/icons/heart.svg"),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Sizes.sidebarHeartIcon.dp)
                )
            }
            // playlist items
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(RoundedCornerShape(Sizes.radiusSm.dp)).background(Color(0xFFd4a373)))
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(RoundedCornerShape(Sizes.radiusSm.dp)).background(Color(0xFF444444)))
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(RoundedCornerShape(Sizes.radiusSm.dp)).background(Color(0xFF555555)))
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(RoundedCornerShape(Sizes.radiusSm.dp)).background(Color(0xFF8a5a44)))
            // artist items
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(CircleShape).background(Color(0xFF333333)))
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(CircleShape).background(Color(0xFF444444)))
            Box(Modifier.size(Sizes.sidebarThumbSize.dp).clip(CircleShape).background(Color(0xFF555555)))
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
    modifier: Modifier = Modifier
) {
    val hoverSrc = remember { MutableInteractionSource() }
    val isHovered by hoverSrc.collectIsHoveredAsState()

    Column(
        modifier = modifier
            .hoverable(hoverSrc)
    ) {
        // header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Sizes.rightPanelPadding.dp, end = Sizes.rightPanelPadding.dp, top = Sizes.sidebarHeaderTop.dp, bottom = Sizes.sidebarHeaderBottom.dp)
        ) {
            if (isHovered) {
                Icon(
                    painter = painterResource("/icons/chevron_back.svg"),
                    contentDescription = "Collapse sidebar",
                    tint = TextColor,
                    modifier = Modifier
                        .size(Sizes.sidebarIconSm.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleCollapse
                        )
                )
                Spacer(Modifier.width(Sizes.sidebarItemPadH.dp))
            }
            AnimatedVisibility(
                visible = isWide,
                enter = expandHorizontally(tween(600)) + fadeIn(tween(600)),
                exit = shrinkHorizontally(tween(600)) + fadeOut(tween(600))
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
            HeaderIcon(painterResource("/icons/add.svg"), "Create", Sizes.sidebarIconLg.dp)
            HeaderIcon(painterResource("/icons/expand.svg"), "Expand library", Sizes.sidebarIconLg.dp)
        }

        // filter pills
        AnimatedVisibility(
            visible = isWide,
            enter = expandVertically(tween(600)) + fadeIn(tween(600)),
            exit = shrinkVertically(tween(600)) + fadeOut(tween(600))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sizes.sidebarFilterPadH.dp)
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
        }

        // search/sort row
        AnimatedVisibility(
            visible = isWide,
            enter = expandVertically(tween(600)) + fadeIn(tween(600)),
            exit = shrinkVertically(tween(600)) + fadeOut(tween(600))
        ) {
            Spacer(Modifier.height(Sizes.sidebarItemGap.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Sizes.sidebarOuterPadH.dp)
                    .height(Sizes.sidebarSearchRowH.dp)
            ) {
                Icon(
                    painter = painterResource("/icons/search.svg"),
                    contentDescription = "Search",
                    tint = DimColor,
                    modifier = Modifier
                        .size(Sizes.sidebarIconMd.dp)
                        .padding(Sizes.sidebarItemPadV.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* todo */ }
                        )
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Recents",
                    color = DimColor,
                    fontSize = FontSizes.sidebarSmall.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource("/icons/list.svg"),
                    contentDescription = "Toggle view",
                    tint = DimColor,
                    modifier = Modifier
                        .size(Sizes.sidebarIconMd.dp)
                        .padding(Sizes.sidebarItemPadV.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* todo */ }
                        )
                )
            }
        }

        // scrollable list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = Sizes.sidebarItemPadV.dp)
        ) {
            when (filterTab) {
                0 -> PlaylistItems(activeView, onNavigate)
                1 -> ArtistItems(activeView, onNavigate)
            }
        }
    }
}

@Composable
private fun PlaylistItems(activeView: View, onNavigate: (View) -> Unit) {
    LibraryItem(
        icon = {
            Icon(
                painter = painterResource("/icons/heart.svg"),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(Sizes.sidebarHeartIcon.dp)
            )
        },
        name = "Liked Songs",
        subtitle = "Playlist • 27 songs",
        isActive = false,
        isPinned = true,
        onClick = { onNavigate(View.Songs) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFFd4a373),
        name = "dolefulness",
        subtitle = "Playlist • huzaa",
        isActive = activeView == View.Playlists,
        onClick = { onNavigate(View.Playlists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF444444),
        name = "gugugaga",
        subtitle = "Playlist • huzaa",
        isActive = false,
        onClick = { onNavigate(View.Playlists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF555555),
        name = "My Playlist #5",
        subtitle = "Playlist • huzaa",
        isActive = false,
        onClick = { onNavigate(View.Playlists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF8a5a44),
        name = "a7x",
        subtitle = "Playlist • huzaa",
        isActive = false,
        onClick = { onNavigate(View.Playlists) }
    )
}

@Composable
private fun ArtistItems(activeView: View, onNavigate: (View) -> Unit) {
    LibraryItem(
        iconPlaceholder = Color(0xFF333333),
        isCircular = true,
        name = "Decalius",
        subtitle = "Artist",
        isActive = false,
        onClick = { onNavigate(View.Artists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF444444),
        isCircular = true,
        name = "Sadness",
        subtitle = "Artist",
        isActive = false,
        onClick = { onNavigate(View.Artists) }
    )
    LibraryItem(
        iconPlaceholder = Color(0xFF555555),
        isCircular = true,
        name = "Bring Me The Horizon",
        subtitle = "Artist",
        isActive = false,
        onClick = { onNavigate(View.Artists) }
    )
}


@Composable
private fun HeaderIcon(painter: Painter, desc: String, size: Dp = Sizes.sidebarIconMd.dp) {
    Icon(
        painter = painter,
        contentDescription = desc,
        tint = DimColor,
        modifier = Modifier
            .size(size)
            .padding(Sizes.sidebarItemPadV.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* todo */ }
            )
    )
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Sizes.radiusPill.dp))
            .background(if (selected) Color(0xFF2a2a2a) else Color(0xFF141414))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Sizes.sidebarFilterPadH.dp, vertical = Sizes.queueItemPadV.dp)
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
    onClick: () -> Unit
) {
    val bgColor = if (isActive) ActiveBg else Color.Transparent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Sizes.sidebarOuterPadH.dp)
            .clip(RoundedCornerShape(Sizes.radiusSm.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = Sizes.sidebarItemPadH.dp, vertical = Sizes.sidebarItemPadV.dp)
    ) {
        Box(
            modifier = Modifier
                .size(Sizes.sidebarThumbSize.dp)
                .clip(if (isCircular) CircleShape else RoundedCornerShape(Sizes.radiusSm.dp))
                .background(iconPlaceholder),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            }
        }

        Spacer(Modifier.width(Sizes.sidebarItemPadH.dp))

        Column(modifier = Modifier.weight(1f)) {
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
                        modifier = Modifier.size(Sizes.sidebarPinnedIcon.dp)
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
