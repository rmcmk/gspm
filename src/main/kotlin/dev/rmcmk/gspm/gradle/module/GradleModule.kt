package dev.rmcmk.gspm.gradle.module

import org.gradle.tooling.model.Model
import java.io.Serializable

/**
 * Represents a Gradle module.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
interface GradleModule : Model {
    /** The absolute path to the module. */
    val path: String

    /** The version of the module. */
    val coordinate: GradleModuleCoordinate

    /** The child modules of this module. */
    val children: List<GradleModule>
}

/**
 * The default serializable implementation of [GradleModule].
 *
 * @see GradleModule
 * @see Serializable
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
data class DefaultGradleModule(
    override val path: String,
    override val coordinate: GradleModuleCoordinate,
    override val children: List<GradleModule>,
) : GradleModule, Serializable
