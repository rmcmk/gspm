package dev.rmcmk.gspm.gradle.module

import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry

/**
 * A plugin which registers the [GradleModuleBuilder] with the Gradle Tooling API.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
abstract class GradleModuleToolingPlugin : Plugin<Project> {

    /** The registry to register the [GradleModuleBuilder] with. */
    @get:Inject
    abstract val registry: ToolingModelBuilderRegistry

    override fun apply(target: Project) {
        if (target == target.rootProject) {
            registry.register(GradleModuleBuilder())
        }
    }
}
