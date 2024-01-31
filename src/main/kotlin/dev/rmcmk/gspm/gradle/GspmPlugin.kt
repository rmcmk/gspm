package dev.rmcmk.gspm.gradle

import dev.rmcmk.gspm.git.SubmoduleDefinition
import dev.rmcmk.gspm.gradle.module.GradleModule
import dev.rmcmk.gspm.gradle.module.GradleModuleBuilder
import dev.rmcmk.gspm.gradle.module.GradleModuleToolingPlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
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

    override fun apply(target: Settings) {
        target.configureSubmodules()
        target.applyPlugins()
    }

    /**
     * Applies the [GspmProjectPlugin] to the root project. This plugin is required to configure the submodules as
     * included builds.
     *
     * @receiver The settings to apply the plugin to.
     * @see GspmProjectPlugin
     */
    private fun Settings.applyPlugins() {
        gradle.settingsEvaluated {
            gradle.rootProject {
                afterEvaluate {
                    plugins.apply(GspmProjectPlugin::class)
                }
            }
        }
    }

    /**
     * Configures the submodules for the this [Settings]. This method will recursively configure all submodules.
     * The submodule configuration is done by creating a temporary initialization script and injecting it into the
     * submodule's build script and extracting information required to construct a composite build and versioning
     * information for the version catalog.
     *
     * @receiver The settings to configure the submodules for.
     * @see createInitScript
     */
    @Suppress("UnstableApiUsage")
    private fun Settings.configureSubmodules() {
        val root = layout.rootDirectory.toString()
        val file = Path(root, GIT_MODULES_FILE_NAME)

        SubmoduleDefinition.fromFile(file).forEach { submodule ->
            val path = Path(root, submodule.path)

            GradleConnector.newConnector().forProjectDirectory(path.toFile()).connect().use { connection ->
                val temp =
                    createTempFile(path, "gradle-init", ".gradle").apply {
                        writeText(createInitScript())
                    }

                try {
                    val module =
                        connection.model(GradleModule::class)
                            .withArguments("--init-script", temp.absolutePathString()).setStandardOutput(System.out)
                            .setStandardError(System.err).get()

                    // Include the submodule as a composite build.
                    includeBuild(module.path)

                    // Create a version catalog for the submodule and its children.
                    createVersionCatalog(module)
                } finally {
                    temp.deleteIfExists()
                }
            }
        }
    }

    /**
     * Creates a version catalog for the given [module].
     *
     * @param module The module to create the version catalog for.
     */
    private fun Settings.createVersionCatalog(module: GradleModule) {
        // TODO(rmcmk): `gspm` should be configurable, collisions are possible
        dependencyResolutionManagement.versionCatalogs.create("gspm") {
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
            library(name, group, name).version(version)
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
