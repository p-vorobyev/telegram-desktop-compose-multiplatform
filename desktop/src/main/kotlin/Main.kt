import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import auth.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sidebar.Sidebar

@Composable
@Preview
fun App() {
    MaterialTheme {
        Row {
            var waitCode by remember { mutableStateOf(false) }
            var waitPass by remember { mutableStateOf(false) }
            /*var codeSent by remember { mutableStateOf(false) }
            var passSent by remember { mutableStateOf(false) }
            var credentials by remember { mutableStateOf("") }*/
            var status by remember { mutableStateOf(Status.NOT_AUTHORIZED) }

            LaunchedEffect(Unit) {
                status = authorizationStatus()
            }

            val authScope = rememberCoroutineScope()

            authScope.launch {
                while (true) {
                    status = authorizationStatus()
                    if (status == Status.AUTHORIZED) break
                    waitCode = waitCode()
                    waitPass = waitPass()
                    delay(500)
                }
            }

            if (status == Status.AUTHORIZED) {
                Sidebar()
            } else if (waitCode) {
                AuthForm(AuthType.CODE, authScope)
            } else if (waitPass) {
                AuthForm(AuthType.PASSWORD, authScope)
            }
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
