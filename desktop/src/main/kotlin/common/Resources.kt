package common

import java.io.File

object Resources {

    val resourcesDirectory: File = File(System.getProperty("compose.application.resources.dir"))

    val loaderFile: File = resourcesDirectory.resolve("content_loader.gif")

    val backendJar: File = resourcesDirectory.resolve("backend.jar")

}