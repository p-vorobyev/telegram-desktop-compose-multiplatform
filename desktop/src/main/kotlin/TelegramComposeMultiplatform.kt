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
import common.Resources
import io.ktor.client.request.*
import kotlinx.coroutines.*
import scene.composable.InitialLoad
import scene.composable.LoadingDisclaimer
import transport.baseUrl
import transport.clientUri
import transport.httpClient
import java.io.IOException
import java.util.*
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


suspend fun startBackend() = withContext(Dispatchers.IO) {
    val os: String = System.getProperty("os.name")

    val nativeLibPath = Resources.resourcesDirectory().absolutePath
    val backendJar = Resources.resolve("backend-0.0.1.jar").absolutePath
    val backendExecCommand = if (os.lowercase(Locale.getDefault()).startsWith("windows")) {
        "javaw -Xms64m -Xmx256m -Djava.library.path=$nativeLibPath -jar $backendJar"
    } else {
        "nohup java -Xms64m -Xmx256m -Djava.library.path=$nativeLibPath -jar $backendJar >/dev/null 2>&1 &"
    }
    Runtime.getRuntime().exec(backendExecCommand)
    delay(100)
}


private suspend fun awaitReadiness(backendStarted: MutableState<Boolean>) {
    var status: Status? = null
    var startUpReadinessCheckCount = 0
    do {
        try {
            status = authorizationStatus()
            backendStarted.value = true
        } catch (ex: IOException) {
            /*igonre*/
        }
        delay(300)
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
