package app.pulse.desktop.ui.screens.player

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.components.NetworkImage

@Composable
fun PlayerScreen(
    player: PlayerService,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()

    val bg = Color(0xFF0a0a0a)
    val surface = Color(0xFF141414)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)
    val accent = Color(0xFFf2f0eb)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .padding(32.dp)
    ) {
        // Top bar: back + now playing label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(32.dp)
            ) {
                Text("←", color = text, fontSize = 18.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Now Playing",
                color = dim,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        if (state.currentSong == null) {
            // Nothing playing
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Text(
                    text = "No song selected",
                    color = dim,
                    fontSize = 16.sp
                )
            }
            return
        }

        val song = state.currentSong!!

        // Main content: centered thumbnail + info + controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Thumbnail
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = surface),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    song.thumbnail?.let { thumb ->
                        NetworkImage(
                            url = thumb.size(400),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Song title
            Text(
                text = song.info?.name ?: "Untitled",
                color = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            Spacer(Modifier.height(6.dp))

            // Artist
            song.authors?.firstOrNull()?.let { author ->
                Text(
                    text = author.name ?: "",
                    color = dim,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(32.dp))

            // Controls (seek bar, play/pause, volume)
            Controls(
                player = player,
                accent = accent,
                text = text,
                dim = dim,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
        }

        // Loading / error overlay
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Loading...",
                    color = dim,
                    fontSize = 14.sp
                )
            }
        }

        state.error?.let { errorMsg ->
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Error: $errorMsg",
                color = Color(0xFFe74c3c),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
