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
import app.pulse.desktop.ui.components.CardSizes
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
    // i guess this is best fix?
    var discoverResult by remember { mutableStateOf(page?.let { Result.success(it) }) }
    var relatedResult by remember { mutableStateOf<Result<Innertube.RelatedPage?>?>(null) }

    // single sequential fetch no race, seed resolved inside coroutine after page loads
    // android pattern: isSuccess != true = retry on failure too
    LaunchedEffect(Unit) {
        //  fetch discover page if not yet loaded or previous attempt failed
        if (discoverResult?.isSuccess != true) {
            discoverResult = Innertube.discoverPage()
            // notify parent so LayoutShell can pass it down to other screens
            discoverResult?.let { onPageLoaded(it) }
        }

        // resolve seed from loaded discover page (atomic — inside coroutine)
        val seed = discoverResult?.getOrNull()?.trending?.songs?.firstOrNull()?.key
            ?: "J7p4bzqLvCw"

        // fetch related page if not yet loaded or previous attempt failed
        if (relatedResult?.isSuccess != true) {
            relatedResult = Innertube.relatedPage(body = NextBody(videoId = seed))
        }
    }

    val loadedPage = discoverResult?.getOrNull()

    val bg = Color(0xFF0a0a0a)
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
            // quick picks
            relatedResult?.getOrNull()?.let { related ->
                val qpSongs = related.songs
                if (qpSongs != null && qpSongs.isNotEmpty()) {
                    SectionHeader("Quick Picks", {})
                    Spacer(Modifier.height((CardSizes.gapSm * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpSongs.take(20).forEach { songItem ->
                            val song = songItem.toSong()
                            CompactSongCard(
                                song = song,
                                text = text,
                                dim = dim,
                                scale = s,
                                onClick = { onPlaySong(song) }
                            )
                        }
                    }
                    Spacer(Modifier.height((CardSizes.gapLg * s).dp))
                }

                val qpAlbums = related.albums
                if (qpAlbums != null && qpAlbums.isNotEmpty()) {
                    SectionHeader("Related Albums", {})
                    Spacer(Modifier.height((CardSizes.gapMd * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpAlbums.forEach { album ->
                            AlbumCard(album = album, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((CardSizes.gapLg * s).dp))
                }

                val qpArtists = related.artists
                if (qpArtists != null && qpArtists.isNotEmpty()) {
                    SectionHeader("Similar Artists", {})
                    Spacer(Modifier.height((CardSizes.gapSm * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpArtists.forEach { artist ->
                            ArtistCard(artist = artist, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((CardSizes.gapLg * s).dp))
                }

                val qpPlaylists = related.playlists
                if (qpPlaylists != null && qpPlaylists.isNotEmpty()) {
                    SectionHeader("Recommended Playlists", {})
                    Spacer(Modifier.height((CardSizes.gapSm * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        qpPlaylists.forEach { playlist ->
                            PlaylistCard(playlist = playlist, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((CardSizes.gapXl * s).dp))
                }
            } ?: relatedResult?.exceptionOrNull()?.let {
                // silent fail — quick picks unavailable, show discover below
            }

            // discovr
            loadedPage?.let { p ->
                // mooodngenre
                if (p.moods.isNotEmpty()) {
                    SectionHeader("Moods & Genres", onMore = onMoreMoods)
                    Spacer(Modifier.height((CardSizes.gapSm * s).dp))
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
                                    .width((CardSizes.moodW * s).dp)
                                    .height((CardSizes.moodH * s).dp)
                                    .padding(end = (CardSizes.moodEndPad * s).dp)
                                    .clickable { }
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(start = (CardSizes.moodInnerStart * s).dp)
                                ) {
                                    Text(
                                        text = mood.title,
                                        color = moodTextColor,
                                        fontSize = (CardSizes.moodFont * s).sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height((CardSizes.gapLg * s).dp))
                }

                // new
                if (p.newReleaseAlbums.isNotEmpty()) {
                    SectionHeader("New Releases", onMore = onMoreAlbums)
                    Spacer(Modifier.height((CardSizes.gapSm * s).dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        p.newReleaseAlbums.forEach { album ->
                            AlbumCard(album = album, text = text, dim = dim, scale = s)
                        }
                    }
                    Spacer(Modifier.height((CardSizes.gapLg * s).dp))
                }

                // trendink
                if (p.trending.songs.isNotEmpty()) {
                    SectionHeader("Trending", onMore = onMoreTrending)
                    Spacer(Modifier.height((CardSizes.gapSm * s).dp))
                    p.trending.songs.take(15).forEach { songItem ->
                        val song = songItem.toSong()
                        SongCard(song = song, text = text, dim = dim, scale = s, onClick = { onPlaySong(song) })
                    }
                }
            } ?: run {
                // shimmerzz
                MoodsSkeleton(scale = s)
                Spacer(Modifier.height((CardSizes.gapXl * s).dp))
                NewReleasesSkeleton(scale = s)
                Spacer(Modifier.height((CardSizes.gapXl * s).dp))
                TrendingSkeleton(scale = s)
            }
        }
    }
}

// reusabel
@Composable
private fun SectionHeader(title: String, onMore: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color(0xFFf2f0eb),
            fontSize = CardSizes.headerTitle.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onMore) {
            Text(
                text = "More",
                color = Color(0xFFa8a39a),
                fontSize = CardSizes.headerMore.sp
            )
        }
    }
}

@Composable
private fun CompactSongCard(
    song: Song,
    text: Color,
    dim: Color,
    scale: Float,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width((CardSizes.compactSongW * scale).dp)
            .padding(end = (CardSizes.compactSongEndPad * scale).dp)
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
            Column(modifier = Modifier.padding((CardSizes.compactSongInnerPad * scale).dp)) {
                Text(
                    text = song.title,
                    color = text,
                    fontSize = (CardSizes.compactSongTitle * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = (CardSizes.compactSongArt * scale).sp,
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
    text: Color,
    dim: Color,
    scale: Float
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width((CardSizes.albumW * scale).dp)
            .padding(end = (CardSizes.albumEndPad * scale).dp)
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
            Column(modifier = Modifier.padding((CardSizes.albumInnerPad * scale).dp)) {
                Text(
                    text = album.info?.name ?: "Untitled",
                    color = text,
                    fontSize = (CardSizes.albumTitle * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                album.authors?.firstOrNull()?.let { author ->
                    Text(
                        text = author.name ?: "",
                        color = dim,
                        fontSize = (CardSizes.albumAuthor * scale).sp,
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
    text: Color,
    dim: Color,
    scale: Float
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width((CardSizes.artistW * scale).dp)
            .padding(end = (CardSizes.artistEndPad * scale).dp)
            .clickable { }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height((CardSizes.artistVertPad * scale).dp))
            Box(
                modifier = Modifier
                    .size((CardSizes.artistThumb * scale).dp)
                    .clip(RoundedCornerShape((CardSizes.artistThumb / 2).dp))
                    .background(Color(0xFF1a1a1a))
            ) {
                artist.thumbnail?.let { thumb ->
                    NetworkImage(url = thumb.size(100), modifier = Modifier.fillMaxSize())
                }
            }
            Spacer(Modifier.height((10 * scale).dp))
            Text(
                text = artist.info?.name ?: "Unknown",
                color = text,
                fontSize = (CardSizes.artistName * scale).sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = (10 * scale).dp)
            )
            Spacer(Modifier.height((CardSizes.artistVertPad * scale).dp))
        }
    }
}

@Composable
private fun PlaylistCard(
    playlist: Innertube.PlaylistItem,
    text: Color,
    dim: Color,
    scale: Float
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width((CardSizes.playlistW * scale).dp)
            .padding(end = (CardSizes.playlistEndPad * scale).dp)
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
            Column(modifier = Modifier.padding((CardSizes.playlistInnerPad * scale).dp)) {
                Text(
                    text = playlist.info?.name ?: "Untitled",
                    color = text,
                    fontSize = (CardSizes.playlistName * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${playlist.songCount ?: 0} songs",
                    color = dim,
                    fontSize = (CardSizes.playlistCount * scale).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
