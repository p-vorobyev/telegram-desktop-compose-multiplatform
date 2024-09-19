package common

object BackendCommands {

    private val nativeLibPath = Resources.resourcesDirectory.absolutePath

    private val backendJar = Resources.backendJar.absolutePath

    val startWindows =
        "javaw -Xms64m -Xmx256m -XX:+UseStringDeduplication -Djava.library.path=$nativeLibPath -jar $backendJar"

    val startNix =
        "nohup java -Xms64m -Xmx256m -XX:+UseStringDeduplication -Djava.library.path=$nativeLibPath -jar $backendJar >/dev/null 2>&1 &"
}