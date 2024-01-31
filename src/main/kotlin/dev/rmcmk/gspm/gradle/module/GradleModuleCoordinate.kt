package dev.rmcmk.gspm.gradle.module

import java.io.Serializable

/**
 * Represents a Gradle module coordinate. (GAV Coordinate Notation)
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
interface GradleModuleCoordinate {
    /** The name of this module. */
    val name: String

    /** The group of this module. */
    val group: String

    /** The version of this module. */
    val version: String
}

/**
 * The default serializable implementation of [GradleModuleCoordinate].
 *
 * @see Serializable
 * @see GradleModuleCoordinate
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
data class DefaultGradleModuleCoordinate(
    override val name: String,
    override val group: String,
    override val version: String,
) : GradleModuleCoordinate, Serializable
