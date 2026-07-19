package app.pulse.desktop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService

@Composable
fun ContextPanel(
    player: PlayerService,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()
    val song = state.currentSong
    val queue = state.queue
    val currentIndex = state.currentIndex

    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
            // Now Playing header
            Text(
                text = "Now Playing",
                color = dim,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(16.dp))

            //  art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1a1a1a)),
                contentAlignment = Alignment.Center
            ) {
                song?.thumbnailUrl?.let { thumb ->
                    NetworkImage(url = thumb, modifier = Modifier.fillMaxSize())
                }
                if (song == null) {
                    Text(
                        text = "No track selected",
                        color = dim,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Song title
            Text(
                text = song?.title ?: "\u2014",
                color = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // Artist
            Text(
                text = song?.artistsText ?: "",
                color = dim,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (queue.size > 1) {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFF2a2a2a))
                Spacer(Modifier.height(16.dp))

                // queue
                Text(
                    text = "Next in queue",
                    color = dim,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )

                Spacer(Modifier.height(12.dp))

                val upcoming = queue.drop(currentIndex + 1).take(3)
                upcoming.forEachIndexed { i, nextSong ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // mini thumb
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF1a1a1a))
                        ) {
                            nextSong.thumbnailUrl?.let { thumb ->
                                NetworkImage(url = thumb, modifier = Modifier.size(36.dp))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nextSong.title,
                                color = text,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            nextSong.artistsText?.let { author ->
                                Text(
                                    text = author,
                                    color = dim,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    if (i < upcoming.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
