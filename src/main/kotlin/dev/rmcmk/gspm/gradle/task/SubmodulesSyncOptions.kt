package dev.rmcmk.gspm.gradle.task

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.options.Option

/**
 * The options for the [SubmodulesSyncTask].
 *
 * @see SubmodulesSyncTask
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
interface SubmodulesSyncOptions {
    @get:Input
    @get:Optional
    @get:Option(option = "recursive", description = "Whether to recursively synchronize nested submodules.")
    val recursive: Property<Boolean>
}
