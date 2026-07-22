package app.pulse.desktop.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import app.pulse.desktop.ui.adaptiveScale
import app.pulse.desktop.ui.components.NetworkImage
import app.pulse.desktop.ui.components.Sizes

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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        val s = adaptiveScale(maxWidth)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((Sizes.playerPad * s).dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size((Sizes.playerBackIcon * s).dp)
                ) {
                    Text("\u2190", color = text, fontSize = (Sizes.playerBackFont * s).sp)
                }
                Spacer(Modifier.width((8 * s).dp))
                Text(
                    text = "Now Playing",
                    color = dim,
                    fontSize = (14 * s).sp
                )
            }

            Spacer(Modifier.height((Sizes.playerHeaderGap * s).dp))

            val song = state.currentSong

            if (song == null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    Text(
                        text = "No song selected",
                        color = dim,
                        fontSize = (16 * s).sp
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Card(
                        shape = RoundedCornerShape(Sizes.playerCardRadius.dp),
                    colors = CardDefaults.cardColors(containerColor = surface),
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .sizeIn(maxWidth = (Sizes.playerCardSize * s).dp, maxHeight = (Sizes.playerCardSize * s).dp)
                        .aspectRatio(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        song.thumbnailUrl?.let { thumb ->
                            NetworkImage(
                                url = thumb,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(Sizes.playerCardRadius.dp))
                            )
                        }
                    }
                }

                Spacer(Modifier.height((Sizes.playerTitleGap * s).dp))

                Text(
                    text = song.title,
                    color = text,
                    fontSize = (Sizes.playerTitleFont * s).sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .widthIn(max = (Sizes.playerTitleMaxW * s).dp)
                )

                Spacer(Modifier.height((Sizes.playerArtistGap * s).dp))

                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = (Sizes.playerInfoFont * s).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height((Sizes.playerControlsGap * s).dp))

                Controls(
                    player = player,
                    accent = text,
                    text = text,
                    dim = dim,
                    scale = s,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .widthIn(max = (Sizes.playerControlsMaxW * s).dp)
                )
                }
            }

            if (state.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size((40 * s).dp),
                        color = text,
                        strokeWidth = (4 * s).dp
                    )
                }
            }

            state.error?.let { errorMsg ->
                Spacer(Modifier.height((Sizes.playerErrorGap * s).dp))
                Text(
                    text = "Error: $errorMsg",
                    color = Color(0xFFe74c3c),
                    fontSize = (Sizes.playerErrorFont * s).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
