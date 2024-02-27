package dev.rmcmk.gradle.api

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

/**
 * Registers a task of type [T] to the [Project].
 *
 * @return The registered [Task].
 * @receiver The [Project] to register the [Task] to.
 * @throws IllegalStateException If the [TaskMetadata] annotation is not found on [T].
 */
inline fun <reified T : Task> Project.register(): TaskProvider<T> {
    val metadata = taskMetadata<T>()
    return tasks.register(metadata.name, T::class.java) {
        group = "gspm"
        description = metadata.description
    }
}
