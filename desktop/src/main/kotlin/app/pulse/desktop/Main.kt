package app.pulse.desktop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import app.pulse.desktop.ui.components.MiniPlayer
import app.pulse.desktop.ui.constants.sizes.Sizes
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
        var searchQuery by remember { mutableStateOf("") }
        var showPlayer by remember { mutableStateOf(false) }
        var showQueue by remember { mutableStateOf(false) }

        Window(
            onCloseRequest = {
                player.dispose()
                exitApplication()
            },
            title = "Pulse",
            state = WindowState(width = Sizes.windowDefaultW.dp, height = Sizes.windowDefaultH.dp)
        ) {
            // set background directly on AWT window to prevent white flash on loonix/wayland
            // i found this on internet but this is not working on hyprland
            // idk about others DE
            // lemme know pls if someone knows how to fix this
            window.background = java.awt.Color(10, 10, 10)
            window.minimumSize = java.awt.Dimension(Sizes.windowMinW, Sizes.windowMinH)

            Box(Modifier.fillMaxSize()) {
                LayoutShell(
                    activeView = activeView,
                    onNavigate = { activeView = it },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    homePage = homePage?.getOrNull(),
                    onPageLoaded = { homePage = it },
                    onPlaySong = { song -> player.play(song) },
                    player = player
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

                // MiniPlayer — always visible, even on top of PlayerScreen
                val playerState by player.state.collectAsState()
                if (playerState.currentSong != null) {
                    MiniPlayer(
                        player = player,
                        onClick = {},
                        onOpenPlayer = { showPlayer = true },
                        onToggleQueue = { showQueue = !showQueue },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(
                                start = Sizes.miniPlayerEndPad.dp,
                                end = Sizes.miniPlayerEndPad.dp,
                                bottom = Sizes.miniPlayerBottomPad.dp
                            )
                    )
                }
            }
        }
    }
}
