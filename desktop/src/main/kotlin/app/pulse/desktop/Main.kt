package app.pulse.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.screens.home.HomeScreen
import app.pulse.desktop.ui.screens.player.PlayerScreen
import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.Innertube.DiscoverPage

fun main() = application {
    val player = remember { PlayerService() }

    DisposableEffect(Unit) {
        onDispose { player.dispose() }
    }

    var homePage by remember { mutableStateOf<Result<DiscoverPage>?>(null) }
    var showPlayer by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = {
            player.dispose()
            exitApplication()
        },
        title = "Pulse",
        state = WindowState(width = 960.dp, height = 680.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            HomeScreen(
                page = homePage?.getOrNull(),
                onPageLoaded = { homePage = it },
                onPlaySong = { song ->
                    player.play(song)
                    showPlayer = true
                }
            )

            AnimatedVisibility(
                visible = showPlayer,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
                modifier = Modifier.fillMaxSize()
            ) {
                PlayerScreen(
                    player = player,
                    onBack = { showPlayer = false }
                )
            }
        }
    }
}
