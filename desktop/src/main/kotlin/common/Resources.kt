package common

import java.io.File

class Resources {

    companion object {

        private val resourcesDirectory: File = File(System.getProperty("compose.application.resources.dir"))

        fun resourcesDirectory(): File = resourcesDirectory

        fun resolve(relative: String) : File = resourcesDirectory.resolve(relative)

    }

}