package app.pulse.desktop.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.core.data.models.Song
import app.pulse.desktop.ui.components.CardSizes
import app.pulse.core.data.utils.toSong
import app.pulse.desktop.ui.adaptiveScale
import app.pulse.desktop.ui.components.MoodsSkeleton
import app.pulse.desktop.ui.components.NetworkImage
import app.pulse.desktop.ui.components.NewReleasesSkeleton
import app.pulse.desktop.ui.components.QuickPicksSkeleton
import app.pulse.desktop.ui.components.TrendingSkeleton
import app.pulse.desktop.ui.components.log
import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.models.bodies.NextBody
import app.pulse.providers.innertube.requests.discoverPage
import app.pulse.providers.innertube.requests.relatedPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val fCacheJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
}

// disk-persisted cache
private object HomeCache {
    var discover: Result<Innertube.DiscoverPage>? = null
    var related: Result<Innertube.RelatedPage?>? = null

    private val cacheDir = File(System.getProperty("java.io.tmpdir"), "pulse-home")
    private val discoverFile = File(cacheDir, "discover.json")
    private val relatedFile = File(cacheDir, "related.json")
    private val ttlMs = 30L * 60 * 1000 // ttl 30 min

    @Serializable
    data class RelatedData(val page: Innertube.RelatedPage? = null)
    @Serializable
    data class DiscoverData(val page: Innertube.DiscoverPage? = null)

    fun loadFromDisk() {
        try {
            if (discoverFile.exists()) {
                val age = System.currentTimeMillis() - discoverFile.lastModified()
                if (age <= ttlMs) {
                    val text = discoverFile.readText()
                    val data = fCacheJson.decodeFromString<DiscoverData>(text)
                    if (data.page != null) {
                        discover = Result.success(data.page)
                        log("HomeCache", "disk: restored discover (moods=${data.page.moods.size})")
                    }
                } else {
                    discoverFile.delete()
                }
            }
        } catch (e: Exception) {
            log("HomeCache", "disk: discover cache error: ${e.message}")
            discoverFile.delete()
        }
        try {
            if (relatedFile.exists()) {
                val age = System.currentTimeMillis() - relatedFile.lastModified()
                if (age <= ttlMs) {
                    val text = relatedFile.readText()
                    val data = fCacheJson.decodeFromString<RelatedData>(text)
                    if (data.page != null) {
                        related = Result.success(data.page)
                        log("HomeCache", "disk: restored related (songs=${data.page.songs?.size})")
                    }
                } else {
                    relatedFile.delete()
                }
            }
        } catch (e: Exception) {
            log("HomeCache", "disk: related cache error: ${e.message}")
            relatedFile.delete()
        }
    }

    fun saveToDisk() {
        cacheDir.mkdirs()
        // save discover
        val d = discover
        if (d?.isSuccess == true && d.getOrNull() != null) {
            try {
                val data = DiscoverData(page = d.getOrNull())
                discoverFile.writeText(fCacheJson.encodeToString(data))
                log("HomeCache", "disk: saved discover page")
            } catch (e: Exception) {
                log("HomeCache", "disk: failed to save discover: ${e.message}")
            }
        }
        // save related
        val r = related
        if (r?.isSuccess == true && r.getOrNull() != null) {
            try {
                val data = RelatedData(page = r.getOrNull())
                relatedFile.writeText(fCacheJson.encodeToString(data))
                log("HomeCache", "disk: saved related page")
            } catch (e: Exception) {
                log("HomeCache", "disk: failed to save related: ${e.message}")
            }
        }
    }
}

@Composable
fun HomeScreen(
    page: Innertube.DiscoverPage?,
    onPageLoaded: (Result<Innertube.DiscoverPage>) -> Unit,
    onPlaySong: (Song) -> Unit,
    onMoreMoods: () -> Unit = {},
    onMoreAlbums: () -> Unit = {},
    onMoreTrending: () -> Unit = {}
) {
    // disk cache loads once per JVM session
    remember { HomeCache.loadFromDisk() }

    // init from cache if parent didn't provide fresh page
    var discoverResult by remember {
        mutableStateOf(page?.let { Result.success(it) } ?: HomeCache.discover)
    }
    var relatedResult by remember {
        mutableStateOf<Result<Innertube.RelatedPage?>?>(HomeCache.related)
    }

    // retry with exponential backoff: 1s, 2s, 4s
    // returns null when all attempts exhausted (last error is logged)
    suspend fun <T> retryWithBackoff(
        maxRetries: Int = 2,
        initialDelayMs: Long = 1000L,
        label: String = "",
        block: suspend () -> Result<T>?
    ): Result<T>? {
        var attempt = 0
        while (true) {
            val result = block()
            if (result?.isSuccess == true) return result
            attempt++
            if (attempt > maxRetries) {
                log("HomeScreen", "$label exhausted after $attempt attempts")
                return result
            }
            val delayMs = initialDelayMs * (1L shl attempt)
            log("HomeScreen", "$label attempt $attempt failed, retry in ${delayMs}ms")
            delay(delayMs)
        }
    }

    // parallel fetch: relatedPage starts with fallback seed while discoverPage loads
    LaunchedEffect(Unit) {
        log("HomeScreen", "initial: page=${page != null}, discoverLoaded=${discoverResult?.isSuccess}, relatedLoaded=${relatedResult?.isSuccess}")

        // start discover fetch in background (parallel)
        val discoverJob = launch {
            if (discoverResult?.isSuccess != true) {
                log("HomeScreen", "fetching discoverPage...")
                discoverResult = retryWithBackoff(label = "discoverPage") {
                    Innertube.discoverPage()
                }
                val d = discoverResult
                if (d?.isSuccess == true) {
                    val p = d.getOrNull()
                    log("HomeScreen", "discoverPage OK: moods=${p?.moods?.size}, newReleases=${p?.newReleaseAlbums?.size}, trending=${p?.trending?.songs?.size}")
                    HomeCache.discover = d
                    HomeCache.saveToDisk()
                } else {
                    log("HomeScreen", "discoverPage FAILED: ${d?.exceptionOrNull()?.message}")
                }
                discoverResult?.let { onPageLoaded(it) }
            }
        }

        // start relatedPage with fallback seed IMMEDIATELY (parallel with discover)
        if (relatedResult?.isSuccess != true) {
            log("HomeScreen", "fetching relatedPage with fallback seed (parallel)...")
            val fallbackResult = retryWithBackoff(maxRetries = 1, label = "relatedPage(fallback)") {
                Innertube.relatedPage(body = NextBody(videoId = "J7p4bzqLvCw"))
            }
            if (fallbackResult?.isSuccess == true && fallbackResult.getOrNull() != null) {
                val page = fallbackResult.getOrNull()!!
                log("HomeScreen", "fallback OK: songs=${page.songs?.size}, albums=${page.albums?.size}, artists=${page.artists?.size}, playlists=${page.playlists?.size}")
                relatedResult = fallbackResult
                HomeCache.related = fallbackResult
                HomeCache.saveToDisk()
            }
        }

        // wait for discover to finish, then try real seeds if fallback failed
        discoverJob.join()

        val seeds = discoverResult?.getOrNull()?.trending?.songs?.take(3)?.map { it.key }.orEmpty()
        if (seeds.isNotEmpty() && relatedResult?.isSuccess != true) {
            log("HomeScreen", "trying ${seeds.size} real seeds: $seeds")
            for (seed in seeds) {
                if (relatedResult?.isSuccess == true) break
                val result = retryWithBackoff(maxRetries = 1, label = "relatedPage($seed)") {
                    Innertube.relatedPage(body = NextBody(videoId = seed))
                }
                if (result?.isSuccess == true && result.getOrNull() != null) {
                    val page = result.getOrNull()!!
                    log("HomeScreen", "relatedPage OK from seed=$seed: songs=${page.songs?.size}, albums=${page.albums?.size}, artists=${page.artists?.size}, playlists=${page.playlists?.size}")
                    relatedResult = result
                    HomeCache.related = result
                    HomeCache.saveToDisk()
                    break
                }
                if (result?.isSuccess == true && result.getOrNull() == null) {
                    log("HomeScreen", "seed=$seed returned null, trying next...")
                } else {
                    log("HomeScreen", "seed=$seed FAILED: ${result?.exceptionOrNull()?.message}, trying next...")
                }
            }
        }

        // mark complete if still nothing
        if (relatedResult?.isSuccess != true) {
            log("HomeScreen", "all exhausted, marking related as unavailable")
            relatedResult = Result.success(null)
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
        val availWidth = maxWidth // capture for nested scopes
        val scrollState = rememberScrollState()
        var trendingLimit by remember { mutableStateOf(15) }

        // detect scroll near bottom to load more trending (works if theres still trending)
        val nearBottom by remember {
            derivedStateOf {
                scrollState.maxValue > 0 && scrollState.value >= scrollState.maxValue - 400
            }
        }
        LaunchedEffect(nearBottom) {
            if (nearBottom) {
                val total = loadedPage?.trending?.songs?.size ?: return@LaunchedEffect
                if (trendingLimit < total) {
                    trendingLimit = (trendingLimit + 15).coerceAtMost(total)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
            } ?: run {
                if (relatedResult == null) {
                    // show shimmer so composition tree has slots for quick pick content
                    // when real data loads later, shimmer seamlessly swaps to real cards
                    QuickPicksSkeleton(scale = s)
                }
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

                    // responsive grid
                    val gapDp = (CardSizes.gridGap * s).dp
                    val contentW = availWidth - (48 * s).dp
                    val minCardDp = (CardSizes.gridMinCardW * s).dp
                    val rawCols = (contentW / (minCardDp + gapDp)).toInt()
                        .coerceAtLeast(1)
                    val colCount = rawCols.coerceIn(CardSizes.gridMinCols, CardSizes.gridMaxCols)
                    val cardW = (contentW - gapDp * (colCount - 1)) / colCount
                    val cardH = cardW + (CardSizes.gridTextAreaH * s).dp

                    val totalSongs = p.trending.songs.size
                    val showCount = trendingLimit.coerceAtMost(totalSongs)
                    p.trending.songs.take(showCount).chunked(colCount).forEach { rowSongs ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(gapDp)
                        ) {
                            rowSongs.forEach { songItem ->
                                val song = songItem.toSong()
                                TrendingGridCard(
                                    song = song,
                                    cardWidth = cardW,
                                    cardHeight = cardH,
                                    text = text,
                                    dim = dim,
                                    scale = s,
                                    onClick = { onPlaySong(song) }
                                )
                            }
                        }
                        Spacer(Modifier.height(gapDp))
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
    val cardSize = (CardSizes.cardW * scale).dp
    val cardH = cardSize + (CardSizes.cardTextH * scale).dp
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width(cardSize)
            .height(cardH)
            .padding(end = (CardSizes.cardEndPad * scale).dp)
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
    val cardSize = (CardSizes.cardW * scale).dp
    val cardH = cardSize + (CardSizes.cardTextH * scale).dp
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width(cardSize)
            .height(cardH)
            .padding(end = (CardSizes.cardEndPad * scale).dp)
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
    val cardSize = (CardSizes.cardW * scale).dp
    val cardH = cardSize + (CardSizes.cardTextH * scale).dp
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width(cardSize)
            .height(cardH)
            .padding(end = (CardSizes.cardEndPad * scale).dp)
            .clickable { }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(CardSizes.cardThumbRadius.dp))
                    .background(Color(0xFF1a1a1a))
            ) {
                artist.thumbnail?.let { thumb ->
                    NetworkImage(url = thumb.size(200), modifier = Modifier.fillMaxSize())
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = (CardSizes.artistVertPad * scale).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = artist.info?.name ?: "Unknown",
                    color = text,
                    fontSize = (CardSizes.artistName * scale).sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TrendingGridCard(
    song: Song,
    cardWidth: Dp,
    cardHeight: Dp,
    text: Color,
    dim: Color,
    scale: Float = 1f,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(CardSizes.gridThumbRadius.dp))
                    .background(Color(0xFF1a1a1a))
            ) {
                song.thumbnailUrl?.let { thumb ->
                    NetworkImage(url = thumb, modifier = Modifier.fillMaxSize())
                }
            }
            Column(modifier = Modifier.padding(top = (CardSizes.gridTextGapSm * scale).dp)) {
                Text(
                    text = song.title,
                    color = text,
                    fontSize = (CardSizes.gridTitleFont * scale).sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height((CardSizes.gridTextGapSm * scale).dp))
                song.artistsText?.let { author ->
                    Text(
                        text = author,
                        color = dim,
                        fontSize = (CardSizes.gridArtistFont * scale).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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
    val cardSize = (CardSizes.cardW * scale).dp
    val cardH = cardSize + (CardSizes.cardTextH * scale).dp
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .width(cardSize)
            .height(cardH)
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
