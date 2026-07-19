package app.pulse.desktop.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val shimmerBase = Color(0xFF1a1a1a)
private val shimmerHighlight = Color(0xFF2a2a2a)

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val offset = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(shimmerBase, shimmerHighlight, shimmerBase),
                    start = Offset(offset.value - 200f, 0f),
                    end = Offset(offset.value + 200f, 0f)
                )
            )
    )
}

@Composable
fun ShimmerRounded(
    width: Dp = 120.dp,
    height: Dp = 14.dp,
    radius: Dp = CardSizes.skelShimmerRadius.dp
) {
    ShimmerBox(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(radius))
    )
}

@Composable
fun MoodsSkeleton(scale: Float = 1f) {
    Column {
        ShimmerRounded(width = CardSizes.skelTitleWide.dp * scale, height = CardSizes.skelSectionH.dp * scale, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height((CardSizes.gapMd * scale).dp))
        Row {
            repeat(CardSizes.skelMoodCount) {
                ShimmerBox(
                    modifier = Modifier
                        .width((CardSizes.skelMoodW * scale).dp)
                        .height((CardSizes.skelMoodH * scale).dp)
                        .padding(end = (CardSizes.skelMoodEndPad * scale).dp)
                        .clip(RoundedCornerShape(CardSizes.skelMoodRadius.dp))
                )
            }
        }
    }
}

@Composable
fun NewReleasesSkeleton(scale: Float = 1f) {
    Column {
        ShimmerRounded(width = CardSizes.skelTitleMid.dp * scale, height = CardSizes.skelSectionH.dp * scale, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height((CardSizes.gapMd * scale).dp))
        Row {
            repeat(CardSizes.skelAlbumCount) {
                Column(
                    modifier = Modifier
                        .width((CardSizes.skelAlbumW * scale).dp)
                        .padding(end = (CardSizes.skelAlbumEndPad * scale).dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.skelAlbumRadius.dp))
                    )
                    Spacer(Modifier.height((CardSizes.gapSm * scale).dp))
                    ShimmerRounded(width = CardSizes.skelAlbumNameW.dp * scale, height = CardSizes.skelAlbumNameH.dp * scale, radius = 3.dp)
                    Spacer(Modifier.height((CardSizes.skelTextGapSm * scale).dp))
                    ShimmerRounded(width = CardSizes.skelAlbumAuthorW.dp * scale, height = CardSizes.skelAlbumAuthorH.dp * scale, radius = 3.dp)
                }
            }
        }
    }
}

@Composable
fun QuickPicksSkeleton(scale: Float = 1f) {
    // quick picks songs row
    Column {
        ShimmerRounded(width = CardSizes.skelTitleWide.dp * scale, height = CardSizes.skelSectionH.dp * scale, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height((CardSizes.gapMd * scale).dp))
        Row {
            repeat(CardSizes.skelQpSongCount) {
                Column(
                    modifier = Modifier
                        .width((CardSizes.compactSongW * scale).dp)
                        .padding(end = (CardSizes.compactSongEndPad * scale).dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.skelAlbumRadius.dp))
                    )
                    Spacer(Modifier.height((CardSizes.gapSm * scale).dp))
                    ShimmerRounded(width = (CardSizes.compactSongW * 0.7 * scale).dp, height = (CardSizes.compactSongTitle * 0.7 * scale).dp, radius = 3.dp)
                    Spacer(Modifier.height((CardSizes.skelTextGapSm * scale).dp))
                    ShimmerRounded(width = (CardSizes.compactSongW * 0.4 * scale).dp, height = (CardSizes.compactSongArt * 0.6 * scale).dp, radius = 3.dp)
                }
            }
        }
    }

    Spacer(Modifier.height((CardSizes.gapLg * scale).dp))

    // related albums row
    Column {
        ShimmerRounded(width = CardSizes.skelTitleMid.dp * scale, height = CardSizes.skelSectionH.dp * scale, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height((CardSizes.gapMd * scale).dp))
        Row {
            repeat(CardSizes.skelQpAlbumCount) {
                Column(
                    modifier = Modifier
                        .width((CardSizes.albumW * scale).dp)
                        .padding(end = (CardSizes.albumEndPad * scale).dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.skelAlbumRadius.dp))
                    )
                    Spacer(Modifier.height((CardSizes.gapSm * scale).dp))
                    ShimmerRounded(width = CardSizes.skelAlbumNameW.dp * scale, height = CardSizes.skelAlbumNameH.dp * scale, radius = 3.dp)
                    Spacer(Modifier.height((CardSizes.skelTextGapSm * scale).dp))
                    ShimmerRounded(width = CardSizes.skelAlbumAuthorW.dp * scale, height = CardSizes.skelAlbumAuthorH.dp * scale, radius = 3.dp)
                }
            }
        }
    }
}

@Composable
fun TrendingSkeleton(scale: Float = 1f) {
    Column {
        ShimmerRounded(width = CardSizes.skelTitleNarrow.dp * scale, height = CardSizes.skelSectionH.dp * scale, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height((CardSizes.gapMd * scale).dp))
        Row {
            repeat(CardSizes.skelTrendingCount) {
                val cardW = (CardSizes.gridMinCardW * scale).dp
                Column(
                    modifier = Modifier
                        .width(cardW)
                        .padding(end = (CardSizes.gridGap * scale).dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.gridThumbRadius.dp))
                    )
                    Spacer(Modifier.height((CardSizes.gridTextGapSm * scale).dp))
                    ShimmerRounded(width = (CardSizes.gridMinCardW * 0.8 * scale).dp, height = (CardSizes.gridTitleFont * scale).dp, radius = 3.dp)
                    Spacer(Modifier.height((CardSizes.gridTextGapSm * scale).dp))
                    ShimmerRounded(width = (CardSizes.gridMinCardW * 0.5 * scale).dp, height = (CardSizes.gridArtistFont * scale).dp, radius = 3.dp)
                }
            }
        }
    }
}
