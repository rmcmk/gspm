package dev.rmcmk.gspm.task

import dev.rmcmk.gradle.api.TaskMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * A task which synchronizes, and initializes, if it's the first run of
 * this task, all submodules. This task respects tagged remote refs and
 * will update the submodules to the latest tagged remote ref.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
@TaskMetadata(
    name = "submodulesSync",
    description = "Synchronizes all submodules with their tagged remote ref.",
)
abstract class SubmodulesSyncTask : DefaultTask(), SubmodulesSyncOptions {
    @TaskAction
    fun run() {
        project.exec {
            val command = mutableListOf("git", "submodule", "update", "--init", "--progress", "--remote")
            if (recursive.getOrElse(false)) {
                command.add("--recursive")
            }
            commandLine(command)
        }
    }
}
