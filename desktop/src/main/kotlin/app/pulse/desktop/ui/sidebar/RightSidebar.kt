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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.constants.fonts.FontSizes
import app.pulse.desktop.ui.components.NetworkImage
import app.pulse.desktop.ui.constants.sizes.Sizes

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
            .padding(Sizes.rightPanelPadding.dp),
        verticalArrangement = Arrangement.spacedBy(Sizes.rightPanelPadding.dp)
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
                        .size(Sizes.rightChevronIcon.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onHidePanel
                        )
                )
                Spacer(Modifier.width(Sizes.rightChevronSpacer.dp))
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
                modifier = Modifier.size(Sizes.rightEllipsisIcon.dp)
            )
        }

        // artwork
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Sizes.rightCardRadius.dp))
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
                                .alpha(0.88f),
                            requestedSize = 1440
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
                    .padding(Sizes.sidebarItemPadH.dp)
                    .size(Sizes.rightShareIcon.dp)
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
                Spacer(Modifier.height(Sizes.rightSongArtistGap.dp))
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
                .clip(RoundedCornerShape(Sizes.rightCardRadius.dp))
                .background(cardBg)
                .padding(Sizes.rightCardInnerPad.dp)
        ) {
            Column {
                Text(
                    text = "CREDITS",
                    color = dim,
                    fontSize = FontSizes.rightCredit.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(Sizes.rightCardContentGap.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource("/icons/person.svg"),
                        contentDescription = "Artist",
                        tint = dim,
                        modifier = Modifier.size(Sizes.rightCreditIcon.dp)
                    )
                    Spacer(Modifier.width(Sizes.rightChevronSpacer.dp))
                    Text(
                        text = "Main Artist",
                        color = dim,
                        fontSize = FontSizes.rightLabel.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = song?.artistsText ?: "Unknown",
                        color = text,
                        fontSize = FontSizes.rightCreditsArtist.sp,
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
                    .clip(RoundedCornerShape(Sizes.rightCardRadius.dp))
                    .background(cardBg)
                    .padding(Sizes.rightCardInnerPad.dp)
            ) {
                Column {
                    Text(
                        text = "NEXT IN QUEUE",
                        color = dim,
                        fontSize = FontSizes.rightCredit.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(Sizes.rightCardContentGap.dp))

                    val upcoming = queue.drop(currentIndex + 1).take(1)
                    upcoming.forEachIndexed { i, nextSong ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // mini thumb
                            Box(
                                modifier = Modifier
                                    .size(Sizes.rightQueueThumb.dp)
                                    .clip(RoundedCornerShape(Sizes.rightThumbRadius.dp))
                                    .background(Color(0xFF1a1a1a))
                            ) {
                                val density = LocalDensity.current
                                nextSong.thumbnailUrl?.let { thumb ->
                                    val thumbPx = with(density) { Sizes.rightQueueThumb.dp.toPx().toInt() }
                                    NetworkImage(
                                        url = thumb,
                                        modifier = Modifier.size(Sizes.rightQueueThumb.dp),
                                        requestedSize = thumbPx
                                    )
                                }
                            }
                            Spacer(Modifier.width(Sizes.sidebarItemPadH.dp))
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
                            Spacer(Modifier.height(Sizes.rightItemGap.dp))
                        }
                    }
                }
            }
        }
    }
}
