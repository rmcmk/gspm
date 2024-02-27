package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.checksumMatches
import java.io.File

abstract class Resource {
    init {
        sync()
    }

    abstract val file: File
    abstract val fileName: String
    abstract val content: String

    val path: String get() = file.absolutePath

    fun sync() {
        if (!file.checksumMatches(content)) {
            file.writeText(content)
        }
    }
}
