package app.pulse.desktop.ui

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TextColor = Color(0xFFf2f0eb)
private val DimColor = Color(0xFF686868)
private val ActiveBg = Color(0xFF2a2a2a)
private val ActiveText = Color(0xFFf2f0eb)
private val GreenAccent = Color(0xFF1ed760)


@Composable
fun Sidebar(
    activeView: View,
    onNavigate: (View) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterTab by remember { mutableStateOf(0) } // 0 = Playlists, 1 = Artists

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        // currently is a dummy stuff
        // todo: make it work

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Your Library",
                color = TextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            HeaderIcon(painterResource("/icons/add.svg"), "Create playlist or folder")
            HeaderIcon(painterResource("/icons/chevron_down.svg"), "Collapse")
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            FilterChip(
                label = "Playlists",
                selected = filterTab == 0,
                onClick = { filterTab = 0 }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                label = "Artists",
                selected = filterTab == 1,
                onClick = { filterTab = 1 }
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .height(36.dp)
        ) {
            Icon(
                painter = painterResource("/icons/search.svg"),
                contentDescription = "Search in library",
                tint = DimColor,
                modifier = Modifier
                    .size(28.dp)
                    .padding(4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* todo: search within library */ }
                    )
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = "Recents",
                color = DimColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                painter = painterResource("/icons/list.svg"),
                contentDescription = "Sort view",
                tint = DimColor,
                modifier = Modifier
                    .size(28.dp)
                    .padding(4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* todo: toggle sort */ }
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 4.dp)
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
        icon = { LikedSongsIcon() },
        iconPlaceholder = Color(0xFFb02897),
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
private fun HeaderIcon(painter: Painter, desc: String) {
    Icon(
        painter = painter,
        contentDescription = desc,
        tint = DimColor,
        modifier = Modifier
            .size(32.dp)
            .padding(4.dp)
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
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color(0xFF2a2a2a) else Color(0xFF141414))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) TextColor else DimColor,
            fontSize = 13.sp,
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
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(if (isCircular) CircleShape else RoundedCornerShape(4.dp))
                .background(iconPlaceholder),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                icon()
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    color = if (isActive) GreenAccent else TextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isPinned) {
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        painter = painterResource("/icons/heart.svg"),
                        contentDescription = null,
                        tint = GreenAccent,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            Text(
                text = subtitle,
                color = DimColor,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun LikedSongsIcon() {
    Icon(
        painter = painterResource("/icons/heart.svg"),
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier.size(18.dp)
    )
}
