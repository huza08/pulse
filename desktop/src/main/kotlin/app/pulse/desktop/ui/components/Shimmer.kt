package app.pulse.desktop.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    width: Int = 120,
    height: Int = 14,
    radius: Int = 4
) {
    ShimmerBox(
        modifier = Modifier
            .width(width.dp)
            .height(height.dp)
            .clip(RoundedCornerShape(radius.dp))
    )
}

@Composable
fun MoodsSkeleton() {
    Column {
        ShimmerRounded(width = 160, height = 18, radius = 4)
        Spacer(Modifier.height(12.dp))
        Row {
            repeat(4) {
                ShimmerBox(
                    modifier = Modifier
                        .width(140.dp)
                        .height(56.dp)
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

@Composable
fun NewReleasesSkeleton() {
    Column {
        ShimmerRounded(width = 140, height = 18, radius = 4)
        Spacer(Modifier.height(12.dp))
        Row {
            repeat(4) {
                Column(
                    modifier = Modifier
                        .width(160.dp)
                        .padding(end = 12.dp)
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.height(8.dp))
                    ShimmerRounded(width = 120, height = 12, radius = 3)
                    Spacer(Modifier.height(4.dp))
                    ShimmerRounded(width = 80, height = 10, radius = 3)
                }
            }
        }
    }
}

@Composable
fun TrendingSkeleton() {
    Column {
        ShimmerRounded(width = 120, height = 18, radius = 4)
        Spacer(Modifier.height(12.dp))
        repeat(6) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                ShimmerBox(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerRounded(width = 180, height = 13, radius = 3)
                    Spacer(Modifier.height(6.dp))
                    ShimmerRounded(width = 100, height = 11, radius = 3)
                }
                Spacer(Modifier.width(8.dp))
                ShimmerRounded(width = 32, height = 11, radius = 3)
            }
        }
    }
}
