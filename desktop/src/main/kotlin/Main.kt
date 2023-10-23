import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch

val httpClient = HttpClient(CIO)

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello") }

    val scope = rememberCoroutineScope()

    MaterialTheme {
        Button(onClick = {
            scope.launch {
                println("test")
                val get: HttpResponse = httpClient.get("https://ya.ru")
                println("end")
            }
            text = "Hello World"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    Window(title = "Telegram", onCloseRequest = ::exitApplication.also { httpClient.close() } ) {
        App()
    }
}
