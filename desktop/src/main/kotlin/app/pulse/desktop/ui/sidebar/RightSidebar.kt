package app.pulse.desktop.ui.sidebar

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.components.FontSizes
import app.pulse.desktop.ui.components.NetworkImage

@Composable
fun RightSidebar(
    player: PlayerService,
    onHidePanel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()
    val song = state.currentSong
    val queue = state.queue
    val currentIndex = state.currentIndex

    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)
    val cardBg = Color(0xFF1c1c1c)

    val hoverSrc = remember { MutableInteractionSource() }
    val isHovered by hoverSrc.collectIsHoveredAsState()

    Column(
        modifier = Modifier
            .hoverable(hoverSrc)
            .then(modifier)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isHovered) {
                Icon(
                    painter = painterResource("/icons/chevron_forward.svg"),
                    contentDescription = "Hide panel",
                    tint = text,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onHidePanel
                        )
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = "Now Playing",
                color = text,
                fontSize = FontSizes.rightSection.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource("/icons/ellipsis_horizontal.svg"),
                contentDescription = "More",
                tint = text,
                modifier = Modifier.size(18.dp)
            )
        }

        // artwork
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD946EF)) // pink backdrop
        ) {
            // artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                if (song != null) {
                    song.thumbnailUrl?.let { thumb ->
                        NetworkImage(
                            url = thumb,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.88f)
                        )
                    }
                } else {
                    Text("No track selected", color = dim)
                }
            }

            // share icon top-right
            Icon(
                painter = painterResource("/icons/share_social.svg"),
                contentDescription = "Share",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(18.dp)
            )

        }

        // song identity
        if (song != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = song.title,
                    color = text,
                    fontSize = FontSizes.rightSongTitle.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = FontSizes.rightArtist.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "CREDITS",
                    color = dim,
                    fontSize = FontSizes.rightCredit.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource("/icons/person.svg"),
                        contentDescription = "Artist",
                        tint = dim,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Main Artist",
                        color = dim,
                        fontSize = FontSizes.rightLabel.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = song?.artistsText ?: "Unknown",
                        color = text,
                        fontSize = FontSizes.queueSub.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }


        if (queue.size > 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "NEXT IN QUEUE",
                        color = dim,
                        fontSize = FontSizes.rightCredit.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(12.dp))

                    val upcoming = queue.drop(currentIndex + 1).take(3)
                    upcoming.forEachIndexed { i, nextSong ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // mini thumb
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1a1a1a))
                            ) {
                                nextSong.thumbnailUrl?.let { thumb ->
                                    NetworkImage(url = thumb, modifier = Modifier.size(36.dp))
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = nextSong.title,
                                    color = text,
                                    fontSize = FontSizes.rightLabel.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                nextSong.artistsText?.let { author ->
                                    Text(
                                        text = author,
                                        color = dim,
                                        fontSize = FontSizes.rightNextSub.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (i < upcoming.lastIndex) {
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}
