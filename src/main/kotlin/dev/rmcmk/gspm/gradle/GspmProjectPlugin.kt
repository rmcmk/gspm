package dev.rmcmk.gspm.gradle

import dev.rmcmk.gspm.gradle.api.TaskMetadata
import dev.rmcmk.gspm.gradle.api.taskMetadata
import dev.rmcmk.gspm.gradle.task.SubmodulesListTask
import dev.rmcmk.gspm.gradle.task.SubmodulesSyncTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * A WIP plugin that adds first-class support for Git submodules.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
class GspmProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.register<SubmodulesListTask>()
        target.register<SubmodulesSyncTask>()
    }

    /**
     * Registers a task of type [T] to the project.
     *
     * @throws IllegalStateException If the [TaskMetadata] annotation is not
     *     found on [T].
     * @see TaskMetadata
     * @see Task
     */
    private inline fun <reified T : Task> Project.register() {
        val metadata = taskMetadata<T>()
        tasks.register(metadata.name, T::class.java) {
            group = "gspm"
            description = metadata.description
        }
    }
}
