package dev.rmcmk.gspm.gradle.module

import org.gradle.tooling.model.Model
import java.io.Serializable

/**
 * Represents a serializable Gradle module.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
interface GradleModule : Model, Serializable {
    /** The absolute path to the module. */
    val path: String

    /** The version of the module. */
    val coordinate: GradleModuleCoordinate

    /** The child modules of this module. */
    val children: List<GradleModule>
}

/**
 * The default implementation of [GradleModule].
 *
 * @see GradleModule
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
data class DefaultGradleModule(
    override val path: String,
    override val coordinate: GradleModuleCoordinate,
    override val children: List<GradleModule>,
) : GradleModule
