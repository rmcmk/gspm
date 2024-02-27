package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.checksumMatches
import java.io.File

sealed interface Resource {
    val fileName: String
    val content: String

    companion object {
        inline operator fun <reified T : Resource> invoke(file: File) {
            val resource = T::class.objectInstance ?: error("Resource must be a `data object`")
            val content = resource.content
            if (!file.checksumMatches(content)) {
                file.writeText(content)
            }
        }
    }
}
