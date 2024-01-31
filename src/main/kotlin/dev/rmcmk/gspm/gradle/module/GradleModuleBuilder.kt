package dev.rmcmk.gspm.gradle.module

import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilder

/**
 * A [ToolingModelBuilder] that builds a [GradleModule] model.
 *
 * @see ToolingModelBuilder
 * @see GradleModule
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
class GradleModuleBuilder : ToolingModelBuilder {
    override fun canBuild(modelName: String): Boolean {
        return modelName == MODEL_NAME
    }

    override fun buildAll(
        modelName: String,
        project: Project,
    ): Any {
        return DefaultGradleModule(
            project.projectDir.path,
            project.buildCoordinate(),
            project.buildChildModules(),
        )
    }

    /**
     * Builds the child modules of the project.
     *
     * @return The child modules of the project.
     * @receiver The project to build the child modules of.
     */
    private fun Project.buildChildModules(): List<GradleModule> {
        return subprojects.flatMap { subproject ->
            listOf(
                DefaultGradleModule(
                    subproject.projectDir.path,
                    subproject.buildCoordinate(),
                    subproject.buildChildModules(),
                ),
            )
        }
    }

    /**
     * Builds the [GradleModuleCoordinate] of the project.
     *
     * @return The [GradleModuleCoordinate] of the project.
     * @receiver The project to build the [GradleModuleCoordinate] of.
     */
    private fun Project.buildCoordinate(): GradleModuleCoordinate {
        return DefaultGradleModuleCoordinate(
            name,
            group.toString(),
            version.toString(),
        )
    }

    companion object {
        /** The name of the model this builder builds. */
        val MODEL_NAME: String = GradleModule::class.java.name
    }
}
