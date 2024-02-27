package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.checksumMatches
import java.io.File

interface Resource {
    val fileName: String
    val content: String
    val file: File
    val absolutePath: String
        get() = file.absolutePath

    operator fun invoke() {
        val stream = content.byteInputStream()
        if (!file.exists() || !stream.checksumMatches(file)) {
            val bytes = stream.use { stream.readAllBytes() }
            file.writeBytes(bytes)
        }
    }
}
