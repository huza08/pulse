import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Pulse",
        state = WindowState(width = 960.dp, height = 680.dp)
    ) {
        HomeScreen()
    }
}
