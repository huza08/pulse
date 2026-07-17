package app.pulse.desktop.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import app.pulse.desktop.ui.screens.home.HomeScreen
import app.pulse.desktop.ui.screens.search.SearchScreen
import app.pulse.core.data.models.Song
import app.pulse.providers.innertube.Innertube

@Composable
fun ContentView(
    activeView: View,
    searchQuery: String,
    homePage: Innertube.DiscoverPage?,
    onPageLoaded: (Result<Innertube.DiscoverPage>) -> Unit,
    onPlaySong: (Song) -> Unit
) {
    when (activeView) {
        View.Home -> HomeScreen(
            page = homePage,
            onPageLoaded = onPageLoaded,
            onPlaySong = onPlaySong
        )

        View.Search -> SearchScreen(
            query = searchQuery,
            onPlaySong = onPlaySong
        )
        View.Songs -> PlaceholderView("Songs")
        View.Artists -> PlaceholderView("Artists")
        View.Albums -> PlaceholderView("Albums")
        View.Playlists -> PlaceholderView("Playlists")
    }
}

// ponytail: placeholder, replace with real screen in Phase 4
@Composable
private fun PlaceholderView(title: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "$title — coming soon",
            color = Color(0xFF686868),
            fontSize = 16.sp
        )
    }
}
