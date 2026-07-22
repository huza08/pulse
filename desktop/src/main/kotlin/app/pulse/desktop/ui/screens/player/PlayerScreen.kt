package app.pulse.desktop.ui.screens.player

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.utils.NetworkImage
import app.pulse.desktop.ui.utils.adaptiveScale
import app.pulse.desktop.ui.constants.sizes.Sizes

@Composable
fun PlayerScreen(
    player: PlayerService,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsState()

    val bg = Color(0xFF0a0a0a)
    val surface = Color(0xFF121212)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val s = adaptiveScale(maxWidth)

        // full background artwork
        val songBg = state.currentSong
        val density = LocalDensity.current
        val maxPx = with(density) { maxWidth.toPx().toInt() }
        songBg?.thumbnailUrl?.let { url ->
            NetworkImage(
                url = url,
                modifier = Modifier.fillMaxSize(),
                requestedSize = maxPx.coerceAtLeast(1920)
            )
        } ?: Box(modifier = Modifier.fillMaxSize().background(bg))

        // dim overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // gradient overlay for bottom fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.25f to Color.Transparent,
                        0.6f to bg.copy(alpha = 0.8f),
                        0.8f to bg
                    )
                )
        )

        // content on top
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // top bar area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((Sizes.playerTopBarH * s).dp)
            ) {
                // gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((Sizes.playerTopBarH * s * 2f).dp)
                        .background(
                            Brush.verticalGradient(
                                0.0f to bg,
                                0.15f to bg,
                                0.25f to bg.copy(alpha = 0.9f),
                                0.35f to bg.copy(alpha = 0.75f),
                                0.45f to bg.copy(alpha = 0.6f),
                                0.55f to bg.copy(alpha = 0.45f),
                                0.65f to bg.copy(alpha = 0.3f),
                                0.75f to bg.copy(alpha = 0.2f),
                                1.0f to Color.Transparent
                            )
                        )
                )

                // Row content sits on top
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((Sizes.playerTopBarH * s).dp)
                        .padding(horizontal = (12 * s).dp)
                ) {
                Text(
                    text = state.currentSong?.title ?: "Player",
                    color = text,
                    fontSize = (Sizes.playerTopTitleFont * s).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width((8 * s).dp))
                // right icons with gaps
                IconButton(onClick = {}, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp)) {
                    Icon(painterResource("/icons/volume_up.svg"), "Notifications", tint = dim, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp * 0.75f))
                }
                Spacer(Modifier.width((Sizes.playerTopIconGap * s).dp))
                IconButton(onClick = {}, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp)) {
                    Icon(painterResource("/icons/bookmark_outline.svg"), "Save", tint = dim, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp * 0.75f))
                }
                Spacer(Modifier.width((Sizes.playerTopIconGap * s).dp))
                IconButton(onClick = {}, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp)) {
                    Icon(painterResource("/icons/person.svg"), "Profile", tint = dim, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp * 0.75f))
                }
                Spacer(Modifier.width((Sizes.playerTopIconGap * s).dp))
                IconButton(onClick = {}, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp)) {
                    Icon(painterResource("/icons/ellipsis_horizontal.svg"), "Menu", tint = dim, modifier = Modifier.size((Sizes.playerTopIconSize * s).dp * 0.75f))
                }
                Spacer(Modifier.width((Sizes.playerTopIconGap * s).dp))
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size((Sizes.playerTopIconSize * s).dp)
                ) {
                    Icon(
                        painter = painterResource("/icons/minimize.svg"),
                        contentDescription = "Minimize",
                        tint = text,
                        modifier = Modifier.size((Sizes.playerTopIconSize * s).dp * 0.75f)
                    )
                }
            }
            } // end top bar Box

            // center content
            val song = state.currentSong

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (song == null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "No song playing",
                            color = dim,
                            fontSize = (16 * s).sp
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = (Sizes.playerPad * s).dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // artwork
                            Card(
                                shape = RoundedCornerShape(Sizes.playerCardRadius.dp),
                                colors = CardDefaults.cardColors(containerColor = surface),
                                modifier = Modifier
                                    .sizeIn(
                                        maxWidth = (Sizes.playerCardSize * s).dp,
                                        maxHeight = (Sizes.playerCardSize * s).dp
                                    )
                                    .aspectRatio(1f)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    song.thumbnailUrl?.let { thumb ->
                                        val cardPx = with(density) { (Sizes.playerCardSize * s).dp.toPx().toInt() }
                                        NetworkImage(
                                            url = thumb,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(Sizes.playerCardRadius.dp)),
                                            requestedSize = cardPx
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height((Sizes.playerTitleGap * s).dp))

                            // song info
                            Text(
                                text = song.title,
                                color = text,
                                fontSize = (Sizes.playerTitleFont * s).sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height((Sizes.playerArtistGap * s).dp))

                            song.artistsText?.let { author ->
                                Text(
                                    text = author,
                                    color = dim,
                                    fontSize = (Sizes.playerInfoFont * s).sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


