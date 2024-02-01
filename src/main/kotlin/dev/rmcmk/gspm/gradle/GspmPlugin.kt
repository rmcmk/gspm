package dev.rmcmk.gspm.gradle

import dev.rmcmk.gspm.git.SubmoduleDefinition
import dev.rmcmk.gspm.gradle.module.GradleModule
import dev.rmcmk.gspm.gradle.module.GradleModuleBuilder
import dev.rmcmk.gspm.gradle.module.GradleModuleToolingPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.plugins.PluginInstantiationException
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.model
import org.gradle.tooling.GradleConnector
import java.util.Properties
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.writeText

/**
 * A plugin enables first-class Gradle support for Git submodules.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 */
class GspmPlugin : Plugin<Settings> {
    /** The properties file containing the coordinate information for this plugin. */
    private val properties by lazy {
        javaClass.classLoader.getResourceAsStream("coordinate.properties").use {
            Properties().apply { load(it) }
        }
    }

    override fun apply(target: Settings) =
        target.run {
            val extension = GspmExtension.fromSettings(this)
            gradle.settingsEvaluated {
                configureSubmodules(extension)
                applyPlugins()
            }
        }

    /**
     * Applies the [GspmProjectPlugin] to the root project. This plugin is required to configure the submodules as
     * included builds.
     *
     * @receiver The settings to apply the plugin to.
     * @see GspmProjectPlugin
     */
    private fun Settings.applyPlugins() {
        gradle.rootProject {
            plugins.apply(GspmProjectPlugin::class)
        }
    }

    /**
     * Configures the submodules for the this [Settings]. This method will recursively configure all submodules.
     * The submodule configuration is done by creating a temporary initialization script and injecting it into the
     * submodule's build script and extracting information required to construct a composite build and versioning
     * information for the version catalog.
     *
     * @param extension The extension to configure the submodules for.
     * @receiver The settings to configure the submodules for.
     * @see createInitScript
     */
    @Suppress("UnstableApiUsage")
    private fun Settings.configureSubmodules(extension: GspmExtension) {
        val root = layout.rootDirectory.toString()
        val file = Path(root, GIT_MODULES_FILE_NAME)

        SubmoduleDefinition.fromFile(file).forEach { submodule ->
            val path = Path(root, submodule.path)
            val initScript =
                createTempFile(path, "gradle-init", ".gradle").apply {
                    writeText(createInitScript())

                    // This file is marked for deletion on JVM exit. While this approach may be suitable, it's worth
                    // noting that this plugin is consistently executed within a long-lived Gradle daemon. This setup
                    // has the potential to accumulate numerous temporary files that might not be promptly deleted.
                    // Despite this consideration, we've opted to retain the deletion mechanism and ensure aggressive
                    // cleanup upon completion.
                    toFile().deleteOnExit()
                }

            try {
                GradleConnector.newConnector().forProjectDirectory(path.toFile()).connect().use { connection ->
                    val module =
                        connection.model(GradleModule::class)
                            .withArguments("--init-script", initScript.absolutePathString())
                            .setStandardOutput(System.out)
                            .setStandardError(System.err)
                            .get()

                    // Include the submodule as a composite build.
                    includeBuild(module.path)

                    // Create a version catalog for the submodule and its children.
                    createVersionCatalog(module, extension)
                }
            } catch (cause: Exception) {
                throw PluginInstantiationException("Failed to configure submodule at $path", cause)
            } finally {
                initScript.deleteIfExists()
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
        }

    /**
     * Creates an initialization script for the given [Settings] to include submodule information. To gather details
     * about the included submodule, the script injects the [GradleModuleToolingPlugin] into the submodule's build
     * script. This plugin registers a [GradleModuleBuilder], which builds a [GradleModule]. The model is then used to
     * generate a version catalog for the submodule and provide additional metadata not available by default in Gradle.
     *
     * @receiver The settings to create the initialization script for.
     */
    private fun createInitScript(): String {
        val coordinate = "${getProperty("group")}:${getProperty("name")}:${getProperty("version")}"
        val klass = GradleModuleToolingPlugin::class
        return """
            import ${klass.qualifiedName}
            initscript {
                repositories {
                    mavenCentral()
                    maven { url 'https://jitpack.io' }
                }
                dependencies { classpath '$coordinate' }
            }
            allprojects { apply plugin: ${klass.simpleName} }
            """.trimIndent()
    }

    /**
     * Returns the property value for the given [key].
     *
     * @param key The key to retrieve the value for.
     * @return The value for the given [key].
     * @throws IllegalStateException If the property for the given [key] does not exist.
     */
    private fun getProperty(key: String): String {
        return properties.getProperty(key) ?: error("Property $key not found")
    }

    companion object {
        /**
         * The path to the submodule definitions. This path is not configurable. Git requires its location to be at
         * `$GIT_WORK_TREE/.gitmodules`.
         *
         * @see <a href="https://git-scm.com/docs/gitmodules">.gitmodules spec</a>
         */
        const val GIT_MODULES_FILE_NAME = ".gitmodules"
    }
}
