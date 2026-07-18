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

@Composable
fun SongCard(
    song: Song,
    surface: Color,
    text: Color,
    dim: Color,
    scale: Float = 1f,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = (6 * scale).dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding((10 * scale).dp)
        ) {
            song.thumbnailUrl?.let { thumb ->
                NetworkImage(
                    url = thumb,
                    modifier = Modifier
                        .size((72 * scale).dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(Modifier.width((12 * scale).dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = text,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = (14 * scale).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            song.durationText?.let { dur ->
                Text(
                    text = dur,
                    color = dim,
                    fontSize = (14 * scale).sp,
                    modifier = Modifier.padding(start = (8 * scale).dp)
                )
            }
        }
    }
}
