package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.checksumMatches
import java.io.File

abstract class Resource {
    abstract val file: File
    abstract val fileName: String
    abstract val content: String

    val path: String get() = file.absolutePath

    fun sync() {
        if (!file.checksumMatches(content)) {
            file.writeText(content)
        }
    }

    companion object {
        inline operator fun <reified T : Resource> invoke(
            file: File,
            constructor: (File) -> T,
        ): T = constructor(file).apply { sync() }
    }
}
