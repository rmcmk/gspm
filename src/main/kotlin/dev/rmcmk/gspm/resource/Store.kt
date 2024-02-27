package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.GspmExtension

class Store(gspm: GspmExtension) {
    private val path = gspm.storageDirectory.asFile.get()

    @Suppress("unused")
    private val gitIgnore = GitIgnore(path.resolve(".gitignore"))

    val initScript = InitScript(path.resolve("init.gradle.kts"))

    companion object {
        const val DEFAULT_STORAGE_DIRECTORY = ".gspm"
    }
}
