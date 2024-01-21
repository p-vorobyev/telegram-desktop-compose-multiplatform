import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import auth.api.Status
import auth.api.authorizationStatus
import auth.api.waitCode
import auth.api.waitPass
import auth.composable.AuthForm
import auth.composable.AuthType
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.composable.InitialLoad
import transport.baseUrl
import transport.clientUri
import transport.httpClient
import java.util.concurrent.atomic.AtomicBoolean

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
                while (!terminatingApp.get()) {
                    status = authorizationStatus()
                    if (status == Status.AUTHORIZED) break
                    waitCode = waitCode()
                    delay(300)
                    waitPass = waitPass()
                    delay(300)
                }
            }

            if (status == Status.AUTHORIZED) {
                InitialLoad()
            } else if (waitCode) {
                AuthForm(AuthType.CODE, mainScope)
            } else if (waitPass) {
                AuthForm(AuthType.PASSWORD, mainScope)
            }
        }
    }
}

val terminatingApp = AtomicBoolean(false)

fun main() = application {
    val appScope = rememberCoroutineScope()
    Window(
        title = "Telegram Compose Multiplatform",
        state = WindowState(width = 1500.dp, height = 1000.dp),
        onCloseRequest = {
            terminatingApp.set(true)
            appScope.launch {
                delay(300)
                httpClient.post("${baseUrl}/${clientUri}/shutdown")
            }.invokeOnCompletion {
                exitApplication()
                httpClient.close()
            }
        }
    ) {
        App()
    }
}
