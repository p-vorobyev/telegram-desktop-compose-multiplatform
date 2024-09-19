import androidx.compose.foundation.layout.Row
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
import common.BackendCommands.startNix
import common.BackendCommands.startWindows
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.composable.InitialLoad
import scene.composable.LoadingDisclaimer
import transport.baseUrl
import transport.clientUri
import transport.httpClient
import util.io
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean


val terminatingApp = AtomicBoolean(false)


@Composable
fun App() {
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
                delay(100)
                waitPass = waitPass()
                delay(100)
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


suspend fun startBackend() = io {
    val os: String = System.getProperty("os.name")
    val backendExecCommand = if (os.lowercase().startsWith("windows")) startWindows else startNix
    Runtime.getRuntime().exec(backendExecCommand)
    Unit
}


private suspend fun awaitReadiness(backendStarted: MutableState<Boolean>) {
    var status: Status? = null
    var startUpReadinessCheckCount = 0
    do {
        try {
            status = authorizationStatus()
            backendStarted.value = true
            break
        } catch (ex: IOException) {
            /*igonre*/
        }
        delay(200)
        startUpReadinessCheckCount++
    } while (status == null && startUpReadinessCheckCount <= 60)
    if (!backendStarted.value) {
        throw RuntimeException("Backend is not started. Check log file.")
    }
}


fun main() = application {

    val backendStarted = remember { mutableStateOf(false) }

    val appScope = rememberCoroutineScope()

    appScope.launch {
        startBackend()
        awaitReadiness(backendStarted)
    }

    Window(
        title = "Telegram Compose Multiplatform",
        state = WindowState(width = 1200.dp, height = 800.dp),
        onCloseRequest = {
            terminatingApp.set(true)
            appScope.launch {
                delay(100)
                httpClient.post("${baseUrl}/${clientUri}/shutdown")
            }.invokeOnCompletion {
                exitApplication()
                httpClient.close()
            }
        }
    ) {
        if (backendStarted.value) {
            App()
        } else {
            LoadingDisclaimer("Starting...")
        }
    }

}
