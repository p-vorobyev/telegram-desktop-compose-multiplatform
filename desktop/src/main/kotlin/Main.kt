import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sidebar.ChatPreview
import sidebar.Sidebar

@Composable
@Preview
fun App() {
    var chatPreviews = mutableStateListOf<ChatPreview>(
        ChatPreview("Chat1", "Some message", null),
        ChatPreview("Chat2", "Super story", 2),
        ChatPreview("Chat3", "Super ssssssssdvlnkd;vnekvjbnlqkevbrlkasbdvlna bdvslbas", 10),
        ChatPreview("Chat4", "Super story", null),
        ChatPreview("Chat5", "Super story", null),
    )
    val scope = rememberCoroutineScope()
    scope.launch {
        while (true) {
            delay(5000)
            chatPreviews.add(ChatPreview("AAAAA", "bbbb", null))
            chatPreviews[0].lastMessage = "SDcsdcvlnsnclwjnw"
        }
    }
    MaterialTheme {
        Sidebar(chatPreviews)
    }
}

fun main() = application {
    Window(title = "Telegram", onCloseRequest = {exitApplication(); httpClient.close()} ) {
        App()
    }
}
