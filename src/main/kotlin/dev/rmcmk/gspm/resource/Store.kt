package dev.rmcmk.gspm.resource

import dev.rmcmk.gspm.GspmExtension

class Store(gspm: GspmExtension) {
    private val path = gspm.storageDirectory.asFile.get()

    @Suppress("unused")
    private val gitIgnore = Resource<GitIgnore>(path.resolve(".gitignore"))

    val initScript = Resource<InitScript>(path.resolve("init.gradle.kts"))

    companion object {
        const val DEFAULT_STORAGE_DIRECTORY = ".gspm"
    }
}
