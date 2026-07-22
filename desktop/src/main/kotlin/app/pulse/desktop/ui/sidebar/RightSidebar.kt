package app.pulse.desktop.ui.sidebar

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import app.pulse.core.data.models.Song
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.constants.fonts.FontSizes
import app.pulse.desktop.ui.utils.NetworkImage
import app.pulse.desktop.ui.constants.sizes.Sizes

enum class RightPanelState { COLLAPSED, EXPANDED }

@Composable
fun RightSidebar(
    player: PlayerService,
    panelState: RightPanelState = RightPanelState.EXPANDED,
    onCycleState: () -> Unit = {},
    onPeekChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()
    val song = state.currentSong
    val queue = state.queue
    val currentIndex = state.currentIndex

    val textColor = Color(0xFFf2f0eb)
    val isCollapsed = panelState == RightPanelState.COLLAPSED

    val interactionSrc = remember { MutableInteractionSource() }
    val isHovered by interactionSrc.collectIsHoveredAsState()

    LaunchedEffect(isHovered) { onPeekChange(isHovered) }

    val animSpec = remember { spring<Dp>(dampingRatio = 1f, stiffness = 300f) }

    val density = LocalDensity.current
    val fullW = with(density) { Sizes.panelMinWidth.dp.toPx().roundToInt() }
    val peekOffsetXDp = ((Sizes.panelMinWidth - Sizes.rightIntermediateWidth) / 2).dp
    val animateOffset by animateDpAsState(
        targetValue = if (isCollapsed) peekOffsetXDp else 0.dp,
        animationSpec = animSpec
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .hoverable(interactionSrc)
    ) {
        // Expanded content — always in composition so artwork/images stay loaded
        Box(
            modifier = Modifier
                .offset(x = animateOffset)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = fullW.coerceAtLeast(constraints.maxWidth),
                            maxWidth = fullW.coerceAtLeast(constraints.maxWidth),
                            minHeight = constraints.minHeight,
                            maxHeight = constraints.maxHeight
                        )
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, 0)
                    }
                }
        ) {
            ExpandedRightSidebar(
                song = song,
                queue = queue,
                currentIndex = currentIndex,
                onCycleState = onCycleState,
                showCloseIcon = !isCollapsed
            )
        }

        // Curtain overlay: unhovered=solid, peek=semi, expanded=gone
        val curtainAlpha by animateFloatAsState(
            targetValue = when {
                isCollapsed && !isHovered -> 1f    // collapsed, not hovering = solid
                isCollapsed && isHovered -> 0.8f   // peek = semi-transparent
                else -> 0f                          // expanded = no overlay
            },
            animationSpec = spring(dampingRatio = 1f, stiffness = 300f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0a0a0a).copy(alpha = curtainAlpha))
        )

        // Collapsed icon overlay — always on top of curtain
        if (isCollapsed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = Sizes.sidebarPad.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource("/icons/panel-right-open.svg"),
                    contentDescription = "Show panel",
                    tint = textColor,
                    modifier = Modifier
                        .size(Sizes.sidebarIconSm.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCycleState
                        )
                )
            }
        }
    }
}

@Composable
private fun ExpandedRightSidebar(
    song: Song?,
    queue: List<Song>,
    currentIndex: Int,
    onCycleState: () -> Unit,
    showCloseIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
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
            if (isHovered && showCloseIcon) {
                Icon(
                    painter = painterResource("/icons/panel-right-close.svg"),
                    contentDescription = "Collapse panel",
                    tint = text,
                    modifier = Modifier
                        .size(Sizes.rightChevronIcon.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCycleState
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
                .background(Color(0xFFD946EF))
        ) {
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

        // credits
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

        // queue
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
