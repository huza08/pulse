package app.pulse.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import app.pulse.desktop.ui.LayoutShell
import app.pulse.desktop.ui.View
import app.pulse.desktop.ui.components.QueuePanel
import app.pulse.desktop.ui.screens.player.PlayerScreen
import app.pulse.providers.innertube.Innertube
import app.pulse.providers.innertube.Innertube.DiscoverPage

fun main() {
    val os = System.getProperty("os.name").lowercase()
    if (os.contains("nix") || os.contains("nux")) {
        System.setProperty("sun.awt.noerasebackground", "true")
        System.setProperty("skiko.renderApi", "OPENGL")
        System.setProperty("sun.java2d.opengl", "true")
    }

    application {
        val player = remember { PlayerService() }

        DisposableEffect(Unit) {
            onDispose { player.dispose() }
        }

        var homePage by remember { mutableStateOf<Result<DiscoverPage>?>(null) }
        var activeView by remember { mutableStateOf(View.Home) }
        var showPlayer by remember { mutableStateOf(false) }
        var showQueue by remember { mutableStateOf(false) }

        Window(
            onCloseRequest = {
                player.dispose()
                exitApplication()
            },
            title = "Pulse",
            state = WindowState(width = 960.dp, height = 680.dp)
        ) {
            // set background directly on AWT window to prevent white flash on loonix/wayland
            // i found this on internet but this is not working on hyprland
            // idk about others DE
            // lemme know pls if someone knows how to fix this
            window.background = java.awt.Color(10, 10, 10)

            LayoutShell(
                activeView = activeView,
                onNavigate = { activeView = it },
                homePage = homePage?.getOrNull(),
                onPageLoaded = { homePage = it },
                onPlaySong = { song -> player.play(song) },
                player = player,
                onOpenPlayer = { showPlayer = true },
                onToggleQueue = { showQueue = !showQueue },
            )

            // Player screen overlay (on top of layout shell)
            AnimatedVisibility(
                visible = showPlayer,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(Modifier.fillMaxSize()) {
                    PlayerScreen(
                        player = player,
                        onBack = { showPlayer = false }
                    )

                    QueuePanel(
                        visible = showQueue,
                        player = player,
                        onClose = { showQueue = false }
                    )
                }
            }
        }
    }
}
