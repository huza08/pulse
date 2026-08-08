@file:Suppress("DEPRECATION")

package app.pulse.desktop.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.core.data.models.Song
import app.pulse.desktop.ui.constants.fonts.FontSizes
import app.pulse.desktop.ui.constants.sizes.RightSidebar
import app.pulse.desktop.ui.constants.sizes.Sizes
import app.pulse.desktop.ui.utils.NetworkImage
import app.pulse.desktop.service.PlayerService

@Composable
fun QueuePanel(
    visible: Boolean,
    player: PlayerService,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()
    val queue = state.queue
    val currentIndex = state.currentIndex

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it },
        modifier = modifier
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // dim scrim — click to close
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
            )

            // panel
            Column(
                modifier = Modifier
                    .widthIn(min = Sizes.queueMinWidth.dp, max = Sizes.queueMaxWidth.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF141414))
                    .padding(RightSidebar.padding.dp)
            ) {
                // header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Queue",
                        color = Color(0xFFf2f0eb),
                        fontSize = FontSizes.rightSection.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${queue.size} songs",
                        color = Color(0xFFa8a39a),
                        fontSize = FontSizes.queueSub.sp
                    )
                    Spacer(Modifier.width(Sizes.queueSpacerMd.dp))
                    Icon(
                        painter = painterResource("/icons/close.svg"),
                        contentDescription = "Close",
                        tint = Color(0xFFa8a39a),
                modifier = Modifier
                    .size(Sizes.queueCloseIcon.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onClose
                            )
                    )
                }

                Spacer(Modifier.height(Sizes.queueSpacerMd.dp))
                HorizontalDivider(color = Color(0xFF2a2a2a))
                Spacer(Modifier.height(Sizes.sidebarItemGap.dp))

                if (queue.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = "Queue is empty",
                            color = Color(0xFFa8a39a),
                            fontSize = FontSizes.queueTitle.sp
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        itemsIndexed(queue) { index, song ->
                            val isCurrent = index == currentIndex
                            QueueItem(
                                index = index,
                                song = song,
                                isCurrent = isCurrent,
                                isFirst = index == 0,
                                isLast = index == queue.lastIndex,
                                onClick = {
                                    if (!isCurrent) {
                                        player.playFromQueue(queue, index)
                                    }
                                },
                                onMoveUp = { if (index > 0) player.moveInQueue(index, index - 1) },
                                onMoveDown = {
                                    if (index < queue.lastIndex) player.moveInQueue(index, index + 1)
                                },
                                onRemove = { player.removeFromQueue(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueItem(
    index: Int,
    song: Song,
    isCurrent: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    val bg = if (isCurrent) Color(0xFF1e1e1e) else Color.Transparent
    val text = if (isCurrent) Color(0xFFf2f0eb) else Color(0xFFc8c5c0)
    val dim = Color(0xFFa8a39a)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Sizes.radiusMd.dp))
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = Sizes.queueItemPadV.dp)
    ) {
        // reorder buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Text(
                text = "\u25B2",
                color = if (isFirst) dim.copy(alpha = 0.3f) else dim,
                fontSize = FontSizes.queueSmall.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !isFirst,
                    onClick = onMoveUp
                )
            )
            Text(
                text = "\u25BC",
                color = if (isLast) dim.copy(alpha = 0.3f) else dim,
                fontSize = FontSizes.queueSmall.sp,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !isLast,
                    onClick = onMoveDown
                )
            )
        }

        Spacer(Modifier.width(Sizes.sidebarItemGap.dp))

        // thumbnail
        Box(
            modifier = Modifier
                .size(Sizes.queueThumbSize.dp)
                .clip(RoundedCornerShape(Sizes.queueThumbRadius.dp))
                .background(Color(0xFF2a2a2a))
        ) {
            val density = LocalDensity.current
            song.thumbnailUrl?.let { thumb ->
                val thumbPx = with(density) { Sizes.queueThumbSize.dp.toPx().toInt() }
                NetworkImage(
                    url = thumb,
                    modifier = Modifier.size(Sizes.queueThumbSize.dp),
                    requestedSize = thumbPx
                )
            }
        }

        Spacer(Modifier.width(Sizes.sidebarItemPadH.dp))

        // info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = text,
                fontSize = FontSizes.queueSub.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                if (isCurrent) {
                    Text(
                        text = "NOW • ",
                        color = Color(0xFF4CAF50),
                        fontSize = FontSizes.queueMeta.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = FontSizes.queueMeta.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.width(Sizes.queueItemSpacerSm.dp))

        // duration
        song.durationText?.let { dur ->
            Text(
                text = dur,
                color = dim,                        fontSize = FontSizes.queueMeta.sp
                    )
            Spacer(Modifier.width(Sizes.queueItemSpacerSm.dp))
        }

        // remove
        Icon(
            painter = painterResource("/icons/close.svg"),
            contentDescription = "Remove",
            tint = dim.copy(alpha = 0.6f),
            modifier = Modifier
                .size(18.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRemove
                )
        )
    }
}
