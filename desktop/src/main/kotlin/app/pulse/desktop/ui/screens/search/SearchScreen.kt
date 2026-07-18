package app.pulse.desktop.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pulse.core.data.models.Song
import app.pulse.core.data.utils.toSong
import app.pulse.desktop.ui.adaptiveScale
import app.pulse.desktop.ui.components.SongCard
import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.models.bodies.SearchBody
import app.pulse.providers.innertube.requests.searchPage
import app.pulse.providers.innertube.utils.from

@Composable
fun SearchScreen(
    query: String,
    onPlaySong: (Song) -> Unit
) {
    var searchResults by remember { mutableStateOf<Result<Innertube.ItemsPage<Innertube.SongItem>?>?>(null) }

    val bg = Color(0xFF0a0a0a)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            searchResults = Innertube.searchPage(
                body = SearchBody(
                    query = query,
                    params = Innertube.SearchFilter.Song.value
                ),
                fromMusicShelfRendererContent = Innertube.SongItem.Companion::from
            )
        } else {
            searchResults = null
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
            if (query.isBlank()) {
                Text(
                    text = "Search for songs from YouTube Music",
                    color = dim,
                    fontSize = (14 * s).sp
                )
            } else {
                Text(
                    text = "Search results",
                    color = text,
                    fontSize = (24 * s).sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = (12 * s).dp)
                )

                Spacer(Modifier.height((8 * s).dp))

                searchResults?.getOrNull()?.let { itemsPage ->
                    itemsPage?.items?.forEach { songItem ->
                        val song = songItem.toSong()
                        SongCard(song = song, text = text, dim = dim, scale = s, onClick = { onPlaySong(song) })
                    }
                } ?: searchResults?.exceptionOrNull()?.let {
                    Text("Search failed", color = dim, fontSize = (14 * s).sp)
                }
            }
        }
    }
}
