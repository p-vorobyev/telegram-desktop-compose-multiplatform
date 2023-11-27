import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import sidebar.Sidebar

@Composable
@Preview
fun App() {
    MaterialTheme {
        Row {
            Sidebar()
        }
    }
}

fun main() = application {
    Window(
        title = "Telegram",
        state = WindowState(width = 1200.dp, height = 800.dp),
        onCloseRequest = {exitApplication(); httpClient.close()}
    ) {
        App()
    }
}
