import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import sidebar.ChatPreview
import sidebar.Sidebar
import sidebar.loadChats

@Composable
@Preview
fun App() {
    val chatPreviews = mutableStateListOf<ChatPreview>()
    LaunchedEffect(Unit) {
        chatPreviews.addAll(loadChats())
    }
    MaterialTheme {
        Row {
            Sidebar(chatPreviews)
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
