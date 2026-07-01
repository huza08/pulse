import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Pulse",
        state = WindowState(width = 600.dp, height = 400.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("Pulse in Linux?")
        }
    }
}
