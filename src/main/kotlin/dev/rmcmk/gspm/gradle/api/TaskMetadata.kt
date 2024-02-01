package dev.rmcmk.gspm.gradle.api

import org.gradle.api.Task
import kotlin.reflect.full.findAnnotation

/**
 * An annotation which provides metadata for a [Task].
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
annotation class TaskMetadata(val name: String, val description: String)

/**
 * Retrieves [TaskMetadata] annotation of task of type [T].
 *
 * @return the task metadata
 * @throws IllegalStateException If the [TaskMetadata] annotation is not found on [T].
 */
inline fun <reified T : Task> taskMetadata(): TaskMetadata {
    val klass = T::class
    return klass.findAnnotation<TaskMetadata>()
        ?: error("TaskMetadata annotation not found on ${klass.simpleName}")
}
