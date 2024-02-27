package dev.rmcmk.gspm

import dev.rmcmk.gradle.api.register
import dev.rmcmk.gspm.module.GradleModuleService
import dev.rmcmk.gspm.resource.Store
import dev.rmcmk.gspm.task.SubmodulesListTask
import dev.rmcmk.gspm.task.SubmodulesSyncTask
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create

/**
 * A plugin enables first-class Gradle support for Git submodules.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
abstract class GspmPlugin : Plugin<Settings> {
    @Suppress("UnstableApiUsage")
    override fun apply(settings: Settings) {
        val gspm = settings.extensions.create<GspmExtension>("gspm", settings.layout.settingsDirectory)
        val store = Store(gspm)
        val moduleService = GradleModuleService(store, gspm)
        val versionCatalog =
            settings.dependencyResolutionManagement.versionCatalogs.create(gspm.versionCatalogName.get())

        moduleService.discoverSubmodules(versionCatalog)

        settings.gradle.settingsEvaluated {
            settings.gradle.rootProject {
                register<SubmodulesListTask>()
                register<SubmodulesSyncTask>()
            }
        }
    }
}
