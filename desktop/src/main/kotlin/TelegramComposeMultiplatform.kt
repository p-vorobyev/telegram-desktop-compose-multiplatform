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
import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import scene.composable.InitialLoad
import transport.baseUrl
import transport.clientUri
import transport.httpClient
import java.io.File
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


suspend fun startBackend(backendStarted: MutableState<Boolean>) {
    val resourcesDirectory = File(System.getProperty("compose.application.resources.dir"))
    val os: String = System.getProperty("os.name")

    val nativeLibPath = resourcesDirectory.absolutePath
    val backendJar = resourcesDirectory.resolve("backend-0.0.1.jar").absolutePath
    val backendExecCommand = if (os.lowercase(Locale.getDefault()).startsWith("windows")) {
        "javaw -Xms64m -Xmx256m -Djava.library.path=$nativeLibPath -jar $backendJar"
    } else {
        "nohup java -Xms64m -Xmx256m -Djava.library.path=$nativeLibPath -jar $backendJar >/dev/null 2>&1 &"
    }

    if (!backendStarted.value) {
        Runtime.getRuntime().exec(backendExecCommand)
        delay(3000)
        backendStarted.value = true
    }
}

fun main() = application {

    val backendStarted = remember { mutableStateOf(false) }

    val appScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        startBackend(backendStarted)
    }

    if (backendStarted.value) {
        Window(
            title = "Telegram Compose Multiplatform",
            state = WindowState(width = 1200.dp, height = 800.dp),
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

}
