import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import auth.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sidebar.Sidebar
import sidebar.chatsLoaded

@Composable
@Preview
fun App() {
    MaterialTheme {
        Row {
            var waitCode by remember { mutableStateOf(false) }
            var waitPass by remember { mutableStateOf(false) }
            var status by remember { mutableStateOf(Status.NOT_AUTHORIZED) }

            LaunchedEffect(Unit) {
                status = authorizationStatus()
            }

            val mainScope = rememberCoroutineScope()

            mainScope.launch {
                while (true) {
                    status = authorizationStatus()
                    if (status == Status.AUTHORIZED) break
                    waitCode = waitCode()
                    waitPass = waitPass()
                    delay(500)
                }
            }

            /*var chatLoading by remember { mutableStateOf(true) }

            mainScope.launch {
                while (chatLoading) {
                    chatLoading = !chatsLoaded()
                    delay(500)
                }
            }*/

            if (status == Status.AUTHORIZED) {
                /*if (chatLoading) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Loading chats...")
                    }
                } else {
                    Sidebar()
                }*/
                Sidebar()
            } else if (waitCode) {
                AuthForm(AuthType.CODE, mainScope)
            } else if (waitPass) {
                AuthForm(AuthType.PASSWORD, mainScope)
            }
        }
    }
}

fun main() = application {
    Window(
        title = "Telegram JLV",
        state = WindowState(width = 1200.dp, height = 800.dp),
        onCloseRequest = {exitApplication(); httpClient.close()}
    ) {
        App()
    }
}
