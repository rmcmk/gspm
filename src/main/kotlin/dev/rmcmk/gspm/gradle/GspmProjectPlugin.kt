package dev.rmcmk.gspm.gradle

import dev.rmcmk.gspm.gradle.api.TaskMetadata
import dev.rmcmk.gspm.gradle.api.taskMetadata
import dev.rmcmk.gspm.gradle.submodule.SubmodulesListTask
import dev.rmcmk.gspm.gradle.submodule.SubmodulesSyncTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.tooling.model.kotlin.dsl.KotlinDslModelsParameters.PREPARATION_TASK_NAME

/**
 * A WIP plugin that adds first-class support for Git submodules.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
class GspmProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.registerBuildScriptTask()

        target.register<SubmodulesListTask>()
        target.register<SubmodulesSyncTask>()
    }

    /**
     * Registers [SubmodulesSyncTask] to be executed during buildscript phase
     * of the current project context.
     */
    private fun Project.registerBuildScriptTask() {
        val metadata = taskMetadata<SubmodulesSyncTask>()
        tasks.named(PREPARATION_TASK_NAME).get().finalizedBy(metadata.name)
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
