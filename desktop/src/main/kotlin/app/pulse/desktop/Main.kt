package app.pulse.desktop

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import app.pulse.desktop.service.PlayerService
import app.pulse.desktop.ui.screens.home.HomeScreen
import app.pulse.desktop.ui.screens.player.PlayerScreen
import app.pulse.providers.innertube.Innertube

fun main() = application {
    val player = remember { PlayerService() }

    DisposableEffect(Unit) {
        onDispose { player.dispose() }
    }

    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    Window(
        onCloseRequest = {
            player.dispose()
            exitApplication()
        },
        title = "Pulse",
        state = WindowState(width = 960.dp, height = 680.dp)
    ) {
        when (screen) {
            is Screen.Home -> HomeScreen(
                onPlaySong = { song ->
                    player.play(song)
                    screen = Screen.Player
                }
            )
            is Screen.Player -> PlayerScreen(
                player = player,
                onBack = { screen = Screen.Home }
            )
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data object Player : Screen()
}
