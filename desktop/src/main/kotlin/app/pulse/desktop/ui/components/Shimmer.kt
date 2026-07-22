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
import app.pulse.desktop.ui.constants.sizes.CardSizes
import app.pulse.desktop.ui.constants.sizes.Sizes

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
fun MoodsSkeleton() {
    Column {
        ShimmerRounded(width = CardSizes.skelTitleWide.dp, height = CardSizes.skelSectionH.dp, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height(CardSizes.gapMd.dp))
        Row {
            repeat(CardSizes.skelMoodCount) {
                ShimmerBox(
                    modifier = Modifier
                        .width(CardSizes.skelMoodW.dp)
                        .height(CardSizes.skelMoodH.dp)
                        .padding(end = CardSizes.skelMoodEndPad.dp)
                        .clip(RoundedCornerShape(CardSizes.skelMoodRadius.dp))
                )
            }
        }
    }
}

@Composable
fun NewReleasesSkeleton() {
    Column {
        ShimmerRounded(width = CardSizes.skelTitleMid.dp, height = CardSizes.skelSectionH.dp, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height(CardSizes.gapMd.dp))
        Row {
            repeat(CardSizes.skelAlbumCount) {
                Column(
                    modifier = Modifier
                        .width(CardSizes.skelAlbumW.dp)
                        .padding(end = CardSizes.skelAlbumEndPad.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.skelAlbumRadius.dp))
                    )
                    Spacer(Modifier.height(CardSizes.gapSm.dp))
                    ShimmerRounded(width = CardSizes.skelAlbumNameW.dp, height = CardSizes.skelAlbumNameH.dp, radius = Sizes.shimmerRadiusXs.dp)
                    Spacer(Modifier.height(CardSizes.skelTextGapSm.dp))
                    ShimmerRounded(width = CardSizes.skelAlbumAuthorW.dp, height = CardSizes.skelAlbumAuthorH.dp, radius = Sizes.shimmerRadiusXs.dp)
                }
            }
        }
    }
}

@Composable
fun QuickPicksSkeleton() {
    // quick picks songs row
    Column {
        ShimmerRounded(width = CardSizes.skelTitleWide.dp, height = CardSizes.skelSectionH.dp, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height(CardSizes.gapMd.dp))
        Row {
            repeat(CardSizes.skelQpSongCount) {
                Column(
                    modifier = Modifier
                        .width(CardSizes.compactSongW.dp)
                        .padding(end = CardSizes.compactSongEndPad.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.skelAlbumRadius.dp))
                    )
                    Spacer(Modifier.height(CardSizes.gapSm.dp))
                    ShimmerRounded(width = (CardSizes.compactSongW * 0.7).dp, height = (CardSizes.compactSongTitle * 0.7).dp, radius = Sizes.shimmerRadiusXs.dp)
                    Spacer(Modifier.height(CardSizes.skelTextGapSm.dp))
                    ShimmerRounded(width = (CardSizes.compactSongW * 0.4).dp, height = (CardSizes.compactSongArt * 0.6).dp, radius = Sizes.shimmerRadiusXs.dp)
                }
            }
        }
    }

    Spacer(Modifier.height(CardSizes.gapLg.dp))

    // related albums row
    Column {
        ShimmerRounded(width = CardSizes.skelTitleMid.dp, height = CardSizes.skelSectionH.dp, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height(CardSizes.gapMd.dp))
        Row {
            repeat(CardSizes.skelQpAlbumCount) {
                Column(
                    modifier = Modifier
                        .width(CardSizes.albumW.dp)
                        .padding(end = CardSizes.albumEndPad.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.skelAlbumRadius.dp))
                    )
                    Spacer(Modifier.height(CardSizes.gapSm.dp))
                    ShimmerRounded(width = CardSizes.skelAlbumNameW.dp, height = CardSizes.skelAlbumNameH.dp, radius = Sizes.shimmerRadiusXs.dp)
                    Spacer(Modifier.height(CardSizes.skelTextGapSm.dp))
                    ShimmerRounded(width = CardSizes.skelAlbumAuthorW.dp, height = CardSizes.skelAlbumAuthorH.dp, radius = Sizes.shimmerRadiusXs.dp)
                }
            }
        }
    }
}

@Composable
fun TrendingSkeleton() {
    Column {
        ShimmerRounded(width = CardSizes.skelTitleNarrow.dp, height = CardSizes.skelSectionH.dp, radius = CardSizes.skelShimmerRadius.dp)
        Spacer(Modifier.height(CardSizes.gapMd.dp))
        Row {
            repeat(CardSizes.skelTrendingCount) {
                val cardW = CardSizes.gridMinCardW.dp
                Column(
                    modifier = Modifier
                        .width(cardW)
                        .padding(end = CardSizes.gridGap.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(CardSizes.gridThumbRadius.dp))
                    )
                    Spacer(Modifier.height(CardSizes.gridTextGapSm.dp))
                    ShimmerRounded(width = (CardSizes.gridMinCardW * 0.8).dp, height = CardSizes.gridTitleFont.dp, radius = Sizes.shimmerRadiusXs.dp)
                    Spacer(Modifier.height(CardSizes.gridTextGapSm.dp))
                    ShimmerRounded(width = (CardSizes.gridMinCardW * 0.5).dp, height = CardSizes.gridArtistFont.dp, radius = Sizes.shimmerRadiusXs.dp)
                }
            }
        }
    }
}
