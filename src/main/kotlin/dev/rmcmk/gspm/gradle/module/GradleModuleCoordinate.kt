package dev.rmcmk.gspm.gradle.module

import java.io.Serializable

/**
 * Represents a serializable Gradle module coordinate. (GAV Coordinate Notation)
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
interface GradleModuleCoordinate : Serializable {
    /** The artifact name of this module. */
    val artifact: String

    /** The group of this module. */
    val group: String

    /** The version of this module. */
    val version: String
}

/**
 * The default implementation of [GradleModuleCoordinate].
 *
 * @see GradleModuleCoordinate
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
data class DefaultGradleModuleCoordinate(
    override val artifact: String,
    override val group: String,
    override val version: String,
) : GradleModuleCoordinate
