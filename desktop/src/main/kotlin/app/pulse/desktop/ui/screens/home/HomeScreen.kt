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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import app.pulse.providers.innertube.models.bodies.NextBody
import app.pulse.providers.innertube.requests.discoverPage
import app.pulse.providers.innertube.requests.relatedPage

@Composable
fun HomeScreen(
    page: Innertube.DiscoverPage?,
    onPageLoaded: (Result<Innertube.DiscoverPage>) -> Unit,
    onPlaySong: (Song) -> Unit,
    onMoreMoods: () -> Unit = {},
    onMoreAlbums: () -> Unit = {},
    onMoreTrending: () -> Unit = {}
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

    // quick picks: fetch related page using a trending seed
    var relatedResult by remember { mutableStateOf<Result<Innertube.RelatedPage?>?>(null) }
    val seedId = page?.trending?.songs?.firstOrNull()?.key ?: "J7p4bzqLvCw"
    LaunchedEffect(seedId) {
        if (relatedResult == null) {
            relatedResult = Innertube.relatedPage(body = NextBody(videoId = seedId))
        }
    }

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
            // ── Quick Picks ──
            relatedResult?.getOrNull()?.let { related ->
                val qpSongs = related.songs
                if (qpSongs != null && qpSongs.isNotEmpty()) {
                    SectionHeader("Quick Picks", {})
                    Spacer(Modifier.height((8 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpSongs.take(20).forEach { songItem ->
                            val song = songItem.toSong()
                            CompactSongCard(
                                song = song,
                                surface = surface,
                                text = text,
                                dim = dim,
                                scale = s,
                                onClick = { onPlaySong(song) }
                            )
                        }
                    }
                    Spacer(Modifier.height((16 * s).dp))
                }

                val qpAlbums = related.albums
                if (qpAlbums != null && qpAlbums.isNotEmpty()) {
                    SectionHeader("Related Albums", {})
                    Spacer(Modifier.height((12 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpAlbums.forEach { album ->
                            AlbumCard(album = album, surface = surface, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((16 * s).dp))
                }

                val qpArtists = related.artists
                if (qpArtists != null && qpArtists.isNotEmpty()) {
                    SectionHeader("Similar Artists", {})
                    Spacer(Modifier.height((8 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpArtists.forEach { artist ->
                            ArtistCard(artist = artist, surface = surface, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((16 * s).dp))
                }

                val qpPlaylists = related.playlists
                if (qpPlaylists != null && qpPlaylists.isNotEmpty()) {
                    SectionHeader("Recommended Playlists", {})
                    Spacer(Modifier.height((8 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpPlaylists.forEach { playlist ->
                            PlaylistCard(playlist = playlist, surface = surface, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((28 * s).dp))
                }
            } ?: relatedResult?.exceptionOrNull()?.let {
                // silent fail — quick picks unavailable, show discover below
            }

            // discovr
            page?.let { p ->
                // mooodngenre
                if (p.moods.isNotEmpty()) {
                    SectionHeader("Moods & Genres", onMore = onMoreMoods)
                    Spacer(Modifier.height((8 * s).dp))
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
                    Spacer(Modifier.height((16 * s).dp))
                }

                // new
                if (p.newReleaseAlbums.isNotEmpty()) {
                    SectionHeader("New Releases", onMore = onMoreAlbums)
                    Spacer(Modifier.height((8 * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        p.newReleaseAlbums.forEach { album ->
                            AlbumCard(album = album, surface = surface, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((16 * s).dp))
                }

                // trendink
                if (p.trending.songs.isNotEmpty()) {
                    SectionHeader("Trending", onMore = onMoreTrending)
                    Spacer(Modifier.height((8 * s).dp))
                    p.trending.songs.take(15).forEach { songItem ->
                        val song = songItem.toSong()
                        SongCard(song = song, surface = surface, text = text, dim = dim, scale = s, onClick = { onPlaySong(song) })
                    }
                }
            } ?: run {
                // shimmerzz
                MoodsSkeleton(scale = s)
                Spacer(Modifier.height((28 * s).dp))
                NewReleasesSkeleton(scale = s)
                Spacer(Modifier.height((28 * s).dp))
                TrendingSkeleton(scale = s)
            }
        }
    }
}

//reusabel
@Composable
private fun SectionHeader(title: String, onMore: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color(0xFFf2f0eb),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onMore) {
            Text(
                text = "More",
                color = Color(0xFFa8a39a),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CompactSongCard(
    song: Song,
    surface: Color,
    text: Color,
    dim: Color,
    scale: Float,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        modifier = Modifier
            .width((140 * scale).dp)
            .padding(end = (8 * scale).dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .background(Color(0xFF1a1a1a))
            ) {
                song.thumbnailUrl?.let { thumb ->
                    NetworkImage(url = thumb, modifier = Modifier.fillMaxSize())
                }
            }
            Column(modifier = Modifier.padding((8 * scale).dp)) {
                Text(
                    text = song.title,
                    color = text,
                    fontSize = (11 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = (10 * scale).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: Innertube.AlbumItem,
    surface: Color,
    text: Color,
    dim: Color,
    scale: Float
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        modifier = Modifier
            .width((160 * scale).dp)
            .padding(end = (12 * scale).dp)
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
                    NetworkImage(url = thumb.size(200), modifier = Modifier.fillMaxSize())
                }
            }
            Column(modifier = Modifier.padding((10 * scale).dp)) {
                Text(
                    text = album.info?.name ?: "Untitled",
                    color = text,
                    fontSize = (12 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                album.authors?.firstOrNull()?.let { author ->
                    Text(
                        text = author.name ?: "",
                        color = dim,
                        fontSize = (10 * scale).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistCard(
    artist: Innertube.ArtistItem,
    surface: Color,
    text: Color,
    dim: Color,
    scale: Float
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        modifier = Modifier
            .width((140 * scale).dp)
            .padding(end = (12 * scale).dp)
            .clickable { }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height((12 * scale).dp))
            Box(
                modifier = Modifier
                    .size((80 * scale).dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF1a1a1a))
            ) {
                artist.thumbnail?.let { thumb ->
                    NetworkImage(url = thumb.size(100), modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.height((8 * scale).dp))
            Text(
                text = artist.info?.name ?: "Unknown",
                color = text,
                fontSize = (12 * scale).sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = (8 * scale).dp)
            )
            Spacer(Modifier.height((12 * scale).dp))
        }
    }
}

@Composable
private fun PlaylistCard(
    playlist: Innertube.PlaylistItem,
    surface: Color,
    text: Color,
    dim: Color,
    scale: Float
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = surface),
        modifier = Modifier
            .width((160 * scale).dp)
            .padding(end = (12 * scale).dp)
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
                playlist.thumbnail?.let { thumb ->
                    NetworkImage(url = thumb.size(200), modifier = Modifier.fillMaxSize())
                }
            }
            Column(modifier = Modifier.padding((10 * scale).dp)) {
                Text(
                    text = playlist.info?.name ?: "Untitled",
                    color = text,
                    fontSize = (12 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${playlist.songCount ?: 0} songs",
                    color = dim,
                    fontSize = (10 * scale).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
