package app.pulse.desktop.ui.sidebar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.ui.ResizableHandle
import app.pulse.desktop.ui.View
import app.pulse.desktop.ui.constants.fonts.FontSizes
import app.pulse.desktop.ui.constants.sizes.LeftSidebar
import app.pulse.desktop.ui.constants.sizes.Sizes
import app.pulse.desktop.ui.utils.NetworkImage

private val TextColor = Color(0xFFf2f0eb)
private val DimColor = Color(0xFFa8a39a)
private val CardBg = Color(0xFF121212)
private val BorderColor = Color(0xFF2a2a2a)
private val Accent = Color(0xFF4CAF50)
private val AccentDark = Color(0xFF1B5E20)
private val ChipBg = Color(0xFF232323)
private val ThumbBg = Color(0xFF282828)
private val RowHover = Color.White.copy(alpha = 0.06f)

enum class LibraryKind { PLAYLIST, ARTIST }

data class LibraryItem(
    val title: String,
    val subtitle: String,
    val kind: LibraryKind,
    val image: String? = null,
    val active: Boolean = false,
    val liked: Boolean = false,
    val musicIcon: Boolean = false
)

// ponytail: static mock matching the TSX design; real library items can be
// passed in via the items param once the desktop library data exists.
private val mockLibraryItems = listOf(
    LibraryItem("Liked Songs", "Playlist • 29 songs", LibraryKind.PLAYLIST, liked = true),
    LibraryItem(
        "alesana", "Playlist • huzaa", LibraryKind.PLAYLIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-alesana.png"
    ),
    LibraryItem(
        "dolefulness", "Playlist • huzaa", LibraryKind.PLAYLIST, active = true,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-dolefulness.png"
    ),
    LibraryItem(
        "gugugaga", "Playlist • huzaa", LibraryKind.PLAYLIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-gugugaga.png"
    ),
    LibraryItem("My Playlist #5", "Playlist • huzaa", LibraryKind.PLAYLIST, musicIcon = true),
    LibraryItem(
        "a7x", "Playlist • huzaa", LibraryKind.PLAYLIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-alesana.png"
    ),
    LibraryItem(
        "Decalius", "Playlist • huzaa", LibraryKind.PLAYLIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-gugugaga.png"
    ),
    LibraryItem(
        "Loathe", "Artist", LibraryKind.ARTIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-dolefulness.png"
    ),
    LibraryItem(
        "Sleep Token", "Artist", LibraryKind.ARTIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-alesana.png"
    ),
    LibraryItem(
        "Lorna Shore", "Artist", LibraryKind.ARTIST,
        image = "https://c.animaapp.com/msj45ya4j0esrN/img/sidebar-playlist-gugugaga.png"
    )
)

@Composable
private fun Modifier.noRippleClick(onClick: () -> Unit): Modifier {
    val source = remember { MutableInteractionSource() }
    return clickable(interactionSource = source, indication = null, onClick = onClick)
}

/**
 * Port of the TSX `SidebarLeft` component. All state is internal:
 * collapse/expand with width memory, hover-reveal toggle, drag resize
 * (uncollapses on drag, clamped to [LeftSidebar.minWidth, maxWidth]).
 */
@Composable
fun SidebarLeft(
    items: List<LibraryItem> = mockLibraryItems,
    onNavigate: (View) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isCollapsed by remember { mutableStateOf(false) }
    var sidebarWidth by remember { mutableStateOf(LeftSidebar.defaultWidth.dp) }
    var lastExpandedWidth by remember { mutableStateOf(LeftSidebar.defaultWidth.dp) }
    var activeFilter by remember { mutableStateOf(LibraryKind.PLAYLIST) }
    var selected by remember { mutableStateOf<String?>(null) }

    val hoverSrc = remember { MutableInteractionSource() }
    val isHovered by hoverSrc.collectIsHoveredAsState()

    val width by animateDpAsState(
        targetValue = if (isCollapsed) LeftSidebar.collapsedWidth.dp else sidebarWidth,
        animationSpec = tween(LeftSidebar.widthAnimMs)
    )

    val filteredItems = items.filter { it.kind == activeFilter }

    val toggleCollapsed = {
        if (isCollapsed) {
            isCollapsed = false
            sidebarWidth = lastExpandedWidth
        } else {
            lastExpandedWidth = sidebarWidth
            isCollapsed = true
        }
    }

    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .hoverable(hoverSrc)
    ) {
        // card surface (right-panel match)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(Sizes.radiusMd.dp))
                .background(CardBg, RoundedCornerShape(Sizes.radiusMd.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(Sizes.radiusMd.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Header(
                    isCollapsed = isCollapsed,
                    showToggle = isCollapsed || isHovered,
                    onToggle = toggleCollapsed,
                    onAdd = { onNavigate(View.Playlists) }
                )
                if (!isCollapsed) {
                    FilterChips(active = activeFilter, onSelect = { activeFilter = it })
                }
                SearchRow(
                    isCollapsed = isCollapsed,
                    onSearch = { onNavigate(View.Search) },
                    onAdd = { onNavigate(View.Playlists) }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = if (isCollapsed) LeftSidebar.listPadCollapsedH.dp else LeftSidebar.listPadH.dp)
                ) {
                    filteredItems.forEach { item ->
                        SidebarItem(
                            item = item,
                            isCollapsed = isCollapsed,
                            isActive = item.active || selected == item.title,
                            onClick = {
                                selected = item.title
                                onNavigate(if (item.kind == LibraryKind.PLAYLIST) View.Playlists else View.Artists)
                            }
                        )
                    }
                }
            }
        }

        // resize handle — straddles the card's right edge
        ResizableHandle(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (Sizes.resizerW / 2).dp),
            onDrag = { delta ->
                if (isCollapsed) {
                    // dragging the handle uncollapses (TSX startResizing)
                    isCollapsed = false
                    sidebarWidth = lastExpandedWidth
                } else {
                    val newWidth = (sidebarWidth + delta.dp).coerceIn(
                        LeftSidebar.minWidth.dp,
                        LeftSidebar.maxWidth.dp
                    )
                    sidebarWidth = newWidth
                    lastExpandedWidth = newWidth
                }
            }
        )
    }
}

@Composable
private fun Header(
    isCollapsed: Boolean,
    showToggle: Boolean,
    onToggle: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = LeftSidebar.headerPad.dp,
                end = LeftSidebar.headerPad.dp,
                top = LeftSidebar.headerTop.dp,
                bottom = LeftSidebar.headerBottom.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (isCollapsed) Modifier else Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(
                    if (isCollapsed) "/icons/panel-left-open.svg"
                    else "/icons/panel-left-close.svg"
                ),
                contentDescription = if (isCollapsed) "Expand sidebar" else "Collapse sidebar",
                tint = TextColor,
                modifier = Modifier
                    .size(LeftSidebar.toggleSize.dp)
                    .alpha(if (showToggle) 1f else 0f)
                    .noRippleClick(onToggle)
            )
            if (!isCollapsed) {
                Spacer(Modifier.width(LeftSidebar.headerGap.dp))
                Text(
                    text = "Your Library",
                    color = TextColor,
                    fontSize = FontSizes.sidebarSection.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
        if (!isCollapsed) {
            Box(
                modifier = Modifier
                    .size(LeftSidebar.addBtnSize.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .noRippleClick(onAdd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource("/icons/add.svg"),
                    contentDescription = "Add",
                    tint = DimColor,
                    modifier = Modifier.size(LeftSidebar.iconLg.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChips(active: LibraryKind, onSelect: (LibraryKind) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LeftSidebar.chipGap.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LeftSidebar.headerPad.dp)
            .padding(bottom = LeftSidebar.headerBottom.dp)
    ) {
        FilterChip("Playlists", active == LibraryKind.PLAYLIST) { onSelect(LibraryKind.PLAYLIST) }
        FilterChip("Artists", active == LibraryKind.ARTIST) { onSelect(LibraryKind.ARTIST) }
    }
}

@Composable
private fun FilterChip(label: String, isActive: Boolean, onClick: () -> Unit) {
    val bg = if (isActive) Accent else ChipBg
    val fg = if (isActive) {
        if (Accent.luminance() >= 0.5f) Color.Black else Color.White
    } else {
        TextColor
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Sizes.radiusPill.dp))
            .background(bg, RoundedCornerShape(Sizes.radiusPill.dp))
            .noRippleClick(onClick)
            .padding(horizontal = LeftSidebar.chipPadH.dp, vertical = LeftSidebar.chipPadV.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = FontSizes.sidebarChip.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SearchRow(
    isCollapsed: Boolean,
    onSearch: () -> Unit,
    onAdd: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = LeftSidebar.headerPad.dp)
            .padding(bottom = LeftSidebar.headerBottom.dp)
    ) {
        if (isCollapsed) {
            // collapsed: plus bubble
            Box(
                modifier = Modifier
                    .size(LeftSidebar.plusBubble.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
                    .noRippleClick(onAdd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource("/icons/add.svg"),
                    contentDescription = "Add",
                    tint = TextColor,
                    modifier = Modifier.size(LeftSidebar.iconMd.dp)
                )
            }
        } else {
            Icon(
                painter = painterResource("/icons/search.svg"),
                contentDescription = "Search library",
                tint = DimColor,
                modifier = Modifier
                    .size(LeftSidebar.iconMd.dp)
                    .noRippleClick(onSearch)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LeftSidebar.chipGap.dp)
            ) {
                Text(
                    text = "Recents",
                    color = DimColor,
                    fontSize = FontSizes.sidebarSmall.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource("/icons/list.svg"),
                    contentDescription = "Sort",
                    tint = DimColor,
                    modifier = Modifier.size(LeftSidebar.iconSm.dp)
                )
            }
        }
    }
}

@Composable
private fun SidebarItem(
    item: LibraryItem,
    isCollapsed: Boolean,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val rowSrc = remember { MutableInteractionSource() }
    val isRowHovered by rowSrc.collectIsHoveredAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCollapsed) Arrangement.Center else Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Sizes.radiusSm.dp))
            .background(if (isRowHovered) RowHover else Color.Transparent)
            .hoverable(rowSrc)
            .noRippleClick(onClick)
            .padding(horizontal = LeftSidebar.rowPadH.dp, vertical = LeftSidebar.rowPadV.dp)
    ) {
        // thumbnail
        Box(
            modifier = Modifier
                .size(LeftSidebar.thumbSize.dp)
                .clip(RoundedCornerShape(LeftSidebar.thumbRadius.dp))
                .background(ThumbBg, RoundedCornerShape(LeftSidebar.thumbRadius.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                item.liked -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(Accent, AccentDark)),
                            RoundedCornerShape(LeftSidebar.thumbRadius.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource("/icons/heart.svg"),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(LeftSidebar.iconXl.dp)
                    )
                }

                item.image != null -> NetworkImage(
                    url = item.image,
                    modifier = Modifier.size(LeftSidebar.thumbSize.dp),
                    requestedSize = with(LocalDensity.current) { LeftSidebar.thumbSize.dp.toPx().toInt() }
                )

                item.musicIcon -> Icon(
                    painter = painterResource("/icons/musical_notes.svg"),
                    contentDescription = null,
                    tint = DimColor,
                    modifier = Modifier.size(LeftSidebar.iconXl.dp)
                )
            }
        }

        if (!isCollapsed) {
            Spacer(Modifier.width(LeftSidebar.rowGap.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = if (isActive) Accent else TextColor,
                    fontSize = FontSizes.sidebarItem.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.liked) {
                        Icon(
                            painter = painterResource("/icons/star.svg"),
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(LeftSidebar.starSize.dp)
                        )
                        Spacer(Modifier.width(LeftSidebar.headerGap.dp))
                    }
                    Text(
                        text = item.subtitle,
                        color = DimColor,
                        fontSize = FontSizes.sidebarSub.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
