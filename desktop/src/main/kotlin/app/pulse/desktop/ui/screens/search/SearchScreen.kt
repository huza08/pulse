package app.pulse.desktop.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onPlaySong: (Song) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<Result<Innertube.ItemsPage<Innertube.SongItem>?>?>(null) }
    val scope = rememberCoroutineScope()

    val bg = Color(0xFF0a0a0a)
    val surface = Color(0xFF141414)
    val text = Color(0xFFf2f0eb)
    val dim = Color(0xFFa8a39a)
    val fieldBg = Color(0xFF1a1a1a)
    val fieldBorder = Color(0xFF2a2a2a)

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
                text = "Search",
                color = text,
                fontSize = (24 * s).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = (12 * s).dp)
            )

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search songs...", color = dim, fontSize = (14 * s).sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (query.isNotBlank()) {
                            scope.launch {
                                searchResults = null
                                searchResults = Innertube.searchPage(
                                    body = SearchBody(
                                        query = query,
                                        params = Innertube.SearchFilter.Song.value
                                    ),
                                    fromMusicShelfRendererContent = Innertube.SongItem.Companion::from
                                )
                            }
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = text,
                    unfocusedTextColor = text,
                    cursorColor = text,
                    focusedBorderColor = dim,
                    unfocusedBorderColor = fieldBorder,
                    focusedContainerColor = fieldBg,
                    unfocusedContainerColor = fieldBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height((20 * s).dp))

            if (searchResults != null) {
                searchResults?.getOrNull()?.let { itemsPage ->
                    itemsPage?.items?.forEach { songItem ->
                        val song = songItem.toSong()
                        SongCard(song = song, surface = surface, text = text, dim = dim, scale = s, onClick = { onPlaySong(song) })
                    }
                } ?: searchResults?.exceptionOrNull()?.let {
                    Text("Search failed", color = dim, fontSize = (14 * s).sp)
                }
            } else if (query.isBlank()) {
                Text(
                    text = "Search for songs from YouTube Music",
                    color = dim,
                    fontSize = (14 * s).sp
                )
            }
        }
    }
}
