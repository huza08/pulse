package app.pulse.desktop.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.core.data.models.Song
import androidx.compose.ui.platform.LocalDensity
import app.pulse.desktop.ui.components.CardSizes
import app.pulse.desktop.ui.components.Sizes

@Composable
fun SongCard(
    song: Song,
    text: Color,
    dim: Color,
    scale: Float = 1f,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(Sizes.radiusMd.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = (CardSizes.trendingBottomPad * scale).dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding((CardSizes.trendingRowPad * scale).dp)
        ) {
            val density = LocalDensity.current
            song.thumbnailUrl?.let { thumb ->
                val thumbPx = with(density) { (CardSizes.trendingThumb * scale).dp.toPx().toInt() }
                NetworkImage(
                    url = thumb,
                    modifier = Modifier
                        .size((CardSizes.trendingThumb * scale).dp)
                        .clip(RoundedCornerShape(Sizes.radiusSm.dp)),
                    requestedSize = thumbPx
                )
                Spacer(Modifier.width((CardSizes.trendingGap * scale).dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = text,
                    fontSize = (CardSizes.trendingTitle * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = (CardSizes.trendingSub * scale).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            song.durationText?.let { dur ->
                Text(
                    text = dur,
                    color = dim,
                    fontSize = (CardSizes.trendingSub * scale).sp,
                    modifier = Modifier.padding(start = (CardSizes.trendingDurStartPad * scale).dp)
                )
            }
        }
    }
}
