package dev.rmcmk.gspm

import dev.rmcmk.git.SubmoduleDefinition
import dev.rmcmk.git.parseSubmoduleDefinitions
import dev.rmcmk.gspm.resource.Store.Companion.DEFAULT_STORAGE_DIRECTORY
import org.gradle.api.GradleException
import org.gradle.api.file.Directory
import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class GspmExtension
    @Inject
    constructor(private val path: Directory, objectFactory: ObjectFactory) {
        /** The file that contains the definitions of the submodules. Default: "[Settings.getSettingsDir]/.gitmodules" */
        private val gitModulesFile =
            objectFactory.fileProperty().apply {
                set {
                    path.file(".gitmodules").asFile.also {
                        if (!it.exists()) {
                            throw GradleException(
                                """
                                GspmPlugin: Expected .gitmodules file at ${it.absolutePath} but it does not exist.
                                """.trimIndent(),
                            )
                        }
                    }
                }
                finalizeValueOnRead()
            }

        /** The name of the version catalog to use for the submodules. Default: "submodules". */
        val versionCatalogName =
            objectFactory.property<String>().apply {
                set("submodules")
                finalizeValueOnRead()
            }

        /** The directory where Gspm stores internal configuration, scripts and other files. Default is ".gspm" */
        val storageDirectory =
            objectFactory.fileProperty().apply {
                set {
                    path.dir(DEFAULT_STORAGE_DIRECTORY).asFile.also {
                        it.mkdirs()
                    }
                }
                finalizeValueOnRead()
            }

        /** The definitions of the submodules parsed from the [gitModulesFile]. This is not configurable. */
        val gitSubmoduleDefinitions =
            objectFactory.mapProperty<String, SubmoduleDefinition>().apply {
                set(
                    gitModulesFile.asFile.get().parseSubmoduleDefinitions(path.asFile).also {
                        if (it.isEmpty()) {
                            println("GspmPlugin: Discovered 0 submodules. Skipping configuration.")
                        } else {
                            println("GspmPlugin: Discovered ${it.size} submodules. Configuring...")
                        }
                    },
                )
                disallowChanges()
            }
    }
