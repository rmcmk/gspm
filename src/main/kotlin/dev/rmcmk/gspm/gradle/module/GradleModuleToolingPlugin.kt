package dev.rmcmk.gspm.gradle.module

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import javax.inject.Inject

/**
 * A plugin which registers the [GradleModuleBuilder] with the Gradle
 * Tooling API.
 *
 * @param registry The registry to register the [GradleModuleBuilder] with.
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
abstract class GradleModuleToolingPlugin
    @Inject
    constructor(
        private val registry: ToolingModelBuilderRegistry,
    ) : Plugin<Project> {
        override fun apply(target: Project) {
            if (target == target.rootProject) {
                registry.register(GradleModuleBuilder())
            }
        }
    }
