package dev.rmcmk.gspm.gradle

import dev.rmcmk.gspm.git.SubmoduleDefinition
import dev.rmcmk.gspm.gradle.module.GradleModule
import dev.rmcmk.gspm.gradle.module.GradleModuleBuilder
import dev.rmcmk.gspm.gradle.module.GradleModuleToolingPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.model
import org.gradle.tooling.GradleConnector

/**
 * A plugin enables first-class Gradle support for Git submodules.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
abstract class GspmPlugin : Plugin<Settings> {
    /** The logger for this [GspmPlugin]. */
    private val logger = Logging.getLogger(GspmPlugin::class.java)

    /** The properties of this [GspmPlugin]. */
    private lateinit var properties: GspmProperties

    /**
     * Creates an initialization script to be executed against a [SubmoduleDefinition]. To gather details about the
     * included submodule, the script injects the [GradleModuleToolingPlugin] into the submodule's build script. This
     * plugin registers a [GradleModuleBuilder], which builds a [GradleModule]. The model is then used to generate
     * a version catalog for the submodule and provide additional metadata not available by default in Gradle.
     */
    private val initScriptContents by lazy {
        val klass = GradleModuleToolingPlugin::class
        """
        import ${klass.qualifiedName}
        initscript {
            repositories {
                mavenCentral()
                maven { url 'https://jitpack.io' }
            }
            dependencies { classpath '${properties.coordinate}' }
        }
        allprojects { apply plugin: ${klass.simpleName} }
        """.trimIndent()
    }

    @Suppress("UnstableApiUsage")
    override fun apply(target: Settings) {
        val extension =
            target.extensions.create<GspmExtension>("gspm").apply {
                versionCatalogName.convention("gspm")
            }

        val gitModules = target.layout.rootDirectory.file(GspmProperties.GIT_MODULES_FILE_NAME)
        properties =
            GspmProperties(gitModules).apply {
                if (submodules.isEmpty()) {
                    logger.lifecycle("No submodules found. Skipping configuration.")
                    return
                }

                logger.lifecycle("Configuring ${submodules.size} submodules...")
            }

        with(target.gradle) {
            settingsEvaluated {
                configureSubmodules(extension)

                rootProject {
                    plugins.apply(GspmProjectPlugin::class)

                    logger.lifecycle("Done!")
                }
            }
        }
    }

    /**
     * Configures the submodules for the specified [Settings]. This method will recursively configure all submodules.
     * The submodule configuration is done by creating a temporary initialization script and injecting it into the
     * submodule's build script and extracting information required to construct a composite build and versioning
     * information for the version catalog.
     *
     * @param extension The extension to configure the submodules for.
     * @receiver The settings to configure the submodules for.
     */
    private fun Settings.configureSubmodules(extension: GspmExtension) {
        properties.submodules.forEach { submodule ->
            val relative = submodule.relative(this)
            val initScript = submodule.getInitScript(this, initScriptContents)

            logger.lifecycle("Configuring submodule at $relative")
            try {
                GradleConnector.newConnector().forProjectDirectory(relative).connect().use { connection ->
                    val module =
                        connection.model(GradleModule::class)
                            .withArguments("--init-script", initScript.absolutePath)
                            .setStandardOutput(System.out)
                            .setStandardError(System.err)
                            .get()

                    // Include the submodule as a composite build.
                    includeBuild(module.path)

                    // Create a version catalog for the submodule and its children.
                    createVersionCatalog(module, extension)
                }
            } catch (cause: Exception) {
                logger.error("Failed to configure submodule at $relative", cause)
            } finally {
                initScript.delete()
            }
        }
    }

    /**
     * Creates a version catalog for the given [module].
     *
     * @param module The module to create the version catalog for.
     */
    private fun Settings.createVersionCatalog(
        module: GradleModule,
        extension: GspmExtension,
    ) {
        dependencyResolutionManagement.versionCatalogs.create(extension.versionCatalogName.get()) {
            addLibrary(module)
            module.children.forEach { addLibrary(it) }
        }
    }

    /**
     * Adds a library to the version catalog.
     *
     * @param module The module to add the library for.
     */
    private fun VersionCatalogBuilder.addLibrary(module: GradleModule) =
        module.coordinate.run {
            library(artifact, group, artifact).version(version)
            logger.lifecycle("Added $this to version catalog")
        }
}
