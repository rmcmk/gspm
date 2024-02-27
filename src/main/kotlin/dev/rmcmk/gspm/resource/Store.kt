package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.GspmExtension

class Store(gspm: GspmExtension) {
    private val path = gspm.storageDirectory.asFile.get()

    @Suppress("unused")
    private val gitIgnore = Resource(path.resolve(".gitignore"), ::GitIgnore)

    val initScript = Resource(path.resolve("init.gradle.kts"), ::InitScript)

    companion object {
        const val DEFAULT_STORAGE_DIRECTORY = ".gspm"
    }
}
