package dev.rmcmk.gspm.gradle.submodule

import dev.rmcmk.gspm.gradle.api.TaskMetadata
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * A task that prints all submodules by their remote origin URL.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
@TaskMetadata(
    name = "submodulesList",
    description = "Prints all submodules by their remote origin URL.",
)
abstract class SubmodulesListTask : DefaultTask() {
    @TaskAction
    fun run() {
        project.exec {
            commandLine("git", "submodule", "foreach", "-q", "git", "config", "remote.origin.url")
        }
    }
}
