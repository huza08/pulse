package app.pulse.desktop.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.core.data.models.Song
import app.pulse.core.data.utils.toSong
import app.pulse.desktop.ui.adaptiveScale
import app.pulse.desktop.ui.components.MoodsSkeleton
import app.pulse.desktop.ui.components.NetworkImage
import app.pulse.desktop.ui.components.NewReleasesSkeleton
import app.pulse.desktop.ui.components.SongCard
import app.pulse.desktop.ui.components.TrendingSkeleton
import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.requests.discoverPage

@Composable
fun HomeScreen(
    page: Innertube.DiscoverPage?,
    onPageLoaded: (Result<Innertube.DiscoverPage>) -> Unit,
    onPlaySong: (Song) -> Unit
) {
    LaunchedEffect(Unit) {
        if (page == null) {
            val result = Innertube.discoverPage() ?: return@LaunchedEffect
            onPageLoaded(result)
        }
    }

    val bg = Color(0xFF0a0a0a)
    val surface = Color(0xFF141414)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        val s = adaptiveScale(maxWidth)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = (24 * s).dp, end = (24 * s).dp, top = (24 * s).dp, bottom = (100 * s).dp)
        ) {
            Text(
                text = "Home",
                color = text,
                fontSize = (24 * s).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = (12 * s).dp)
            )

            page?.let { p ->
                if (p.moods.isNotEmpty()) {
                    SectionTitle("Moods & Genres", text, s)
                    Spacer(Modifier.height((12 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        p.moods.sortedBy { it.title }.forEach { mood ->
                            val moodColor = Color(mood.stripeColor)
                            val moodTextColor = if (moodColor.luminance() >= 0.5f) Color.Black else Color.White
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = moodColor),
                                modifier = Modifier
                                    .width((140 * s).dp)
                                    .height((56 * s).dp)
                                    .padding(end = (8 * s).dp)
                                    .clickable { }
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(start = (16 * s).dp)
                                ) {
                                    Text(
                                        text = mood.title,
                                        color = moodTextColor,
                                        fontSize = (13 * s).sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height((28 * s).dp))
                }

                if (p.newReleaseAlbums.isNotEmpty()) {
                    SectionTitle("New Releases", text, s)
                    Spacer(Modifier.height((12 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        p.newReleaseAlbums.forEach { album ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = surface),
                                modifier = Modifier
                                    .width((160 * s).dp)
                                    .padding(end = (12 * s).dp)
                                    .clickable { }
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                            .background(Color(0xFF1a1a1a))
                                    ) {
                                        album.thumbnail?.let { thumb ->
                                            NetworkImage(
                                                url = thumb.size(200),
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.padding((10 * s).dp)) {
                                        Text(
                                            text = album.info?.name ?: "Untitled",
                                            color = text,
                                            fontSize = (12 * s).sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        album.authors?.firstOrNull()?.let { author ->
                                            Text(
                                                text = author.name ?: "",
                                                color = dim,
                                                fontSize = (10 * s).sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height((28 * s).dp))
                }

                if (p.trending.songs.isNotEmpty()) {
                    SectionTitle("Trending", text, s)
                    Spacer(Modifier.height((12 * s).dp))
                    p.trending.songs.take(15).forEach { songItem ->
                        val song = songItem.toSong()
                        SongCard(song = song, surface = surface, text = text, dim = dim, scale = s, onClick = { onPlaySong(song) })
                    }
                }
            } ?: run {
                // Shimmer skeletons while page loads
                MoodsSkeleton(scale = s)
                Spacer(Modifier.height((28 * s).dp))
                NewReleasesSkeleton(scale = s)
                Spacer(Modifier.height((28 * s).dp))
                TrendingSkeleton(scale = s)
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, color: Color, scale: Float = 1f) {
    Text(
        text = title,
        color = color,
        fontSize = (18 * scale).sp,
        fontWeight = FontWeight.SemiBold
    )
}
