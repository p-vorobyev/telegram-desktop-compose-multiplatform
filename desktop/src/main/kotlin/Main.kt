import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sidebar.ChatPreview
import sidebar.Sidebar

val mapper = jacksonObjectMapper()

@Composable
@Preview
fun App() {
    var chatPreviews = mutableStateListOf<ChatPreview>()
    val scope = rememberCoroutineScope()
    scope.launch {
        val json = httpClient.get("http://localhost:8080/client/loadCahts").bodyAsText()
        val loadedChats: List<ChatPreview> = mapper.readValue(json, object : TypeReference<List<ChatPreview>>() {})
        chatPreviews.addAll(loadedChats)
        while (true) {
            delay(1000)
        }
    }
    MaterialTheme {
        Row {
            Sidebar(chatPreviews)
        }
    }
}

fun main() = application {
    Window(title = "Telegram", onCloseRequest = {exitApplication(); httpClient.close()} ) {
        App()
    }
}
