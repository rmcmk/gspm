package dev.rmcmk.gspm.gradle

import dev.rmcmk.gspm.git.SubmoduleDefinition
import dev.rmcmk.gspm.git.parseSubmodules
import dev.rmcmk.gspm.gradle.module.DefaultGradleModuleCoordinate
import dev.rmcmk.gspm.gradle.module.GradleModuleCoordinate
import org.gradle.api.file.RegularFile
import org.gradle.api.initialization.Settings
import java.util.Properties

/**
 * Represents the properties of the Gspm plugin.
 *
 * @param gitModules The file containing the submodule definitions.
 * @param submodules The parsed submodule definitions.
 * @param coordinate The coordinate of the Gradle module.
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
data class GspmProperties internal constructor(
    val gitModules: RegularFile,
    val submodules: List<SubmoduleDefinition>,
    val coordinate: GradleModuleCoordinate,
) {
    companion object {
        /**
         * Creates a new instance of [GspmProperties] from the given [Settings] and [gitModules] file.
         *
         * @param settings The settings to use for the coordinate.
         * @param gitModules The file containing the submodule definitions.
         * @return The new instance of [GspmProperties].
         */
        operator fun invoke(
            settings: Settings,
            gitModules: RegularFile,
        ): GspmProperties {
            val properties =
                settings.javaClass.classLoader.getResourceAsStream("coordinate.properties").use {
                    Properties().apply { load(it) }
                }

            val coordinate =
                DefaultGradleModuleCoordinate(
                    group = properties.getProperty("group") ?: error("Property \"group\" not found"),
                    artifact = properties.getProperty("name") ?: error("Property \"name\" not found"),
                    version = properties.getProperty("version") ?: error("Property \"version\" not found"),
                )

            return GspmProperties(gitModules, gitModules.parseSubmodules(), coordinate)
        }

        /**
         * The path to the submodule definitions. This path is not configurable. Git requires its location to be at
         * `$GIT_WORK_TREE/.gitmodules`.
         *
         * @see <a href="https://git-scm.com/docs/gitmodules">.gitmodules spec</a>
         */
        const val GIT_MODULES_FILE_NAME = ".gitmodules"
    }
}
