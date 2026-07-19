package app.pulse.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// reusable card wrapper, card, thumb + title/subtitle text area
@Composable
fun HomeCard(
    cardWidth: Dp,
    cardHeight: Dp,
    horizontalPadding: Dp = 0.dp,
    endPad: Dp = 0.dp,
    thumbClipShape: RoundedCornerShape = RoundedCornerShape(CardSizes.cardThumbRadius.dp),
    scale: Float,
    onClick: () -> Unit = {},
    thumbnail: @Composable () -> Unit,
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(end = endPad)
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(thumbClipShape)
                    .background(Color(0xFF1a1a1a))
            ) {
                thumbnail()
            }
            Column(
                modifier = Modifier.padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = (CardSizes.thumbTitleGap * scale).dp
                )
            ) {
                title()
                subtitle?.let {
                    Spacer(Modifier.height((CardSizes.titleArtistGap * scale).dp))
                    it()
                }
            }
        }
    }
}
