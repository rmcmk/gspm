package dev.rmcmk.gspm.module

import dev.rmcmk.git.SubmoduleDefinition
import dev.rmcmk.gradle.gmvmb.GradleModule
import dev.rmcmk.gspm.GspmExtension
import dev.rmcmk.gspm.resource.Store
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.model
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.GradleModuleVersion
import java.io.ByteArrayOutputStream
import java.util.LinkedList
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class GradleModuleService(
    private val store: Store,
    private val gspm: GspmExtension,
    private val settings: Settings,
) : AutoCloseable {
    private val executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private val resultQueue = LinkedList<Future<GradleModuleResult>>()
    private val connector = GradleConnector.newConnector().useGradleVersion(settings.gradle.gradleVersion)

    fun discoverSubmodules(versionCatalog: VersionCatalogBuilder) {
        val submodules = gspm.gitSubmoduleDefinitions.get()
        submodules.forEach { (_, definition) ->
            val path = definition.path
            if (!path.exists()) {
                return@forEach
            }

            settings.includeBuild(path)
            queue(definition)
        }

        awaitAll().forEach {
            it.module.addTo(versionCatalog)
        }
    }

    private fun queue(definition: SubmoduleDefinition): Future<GradleModuleResult> {
        val future =
            executorService.submit<GradleModuleResult> {
                connector.forProjectDirectory(definition.path).connect().use {
                    val stdout = ByteArrayOutputStream()
                    val stderr = ByteArrayOutputStream()
                    val module =
                        it.model(GradleModule::class)
                            .withArguments("--init-script", store.initScript.path)
                            .setStandardOutput(stdout)
                            .setStandardError(stderr)
                            .get()
                    GradleModuleResult.create(module, definition, stdout, stderr)
                }
            }
        resultQueue += future
        return future
    }

    private fun awaitAll(): List<GradleModuleResult> {
        return resultQueue.map { it.get() }
    }

    override fun close() {
        connector.disconnect()
        executorService.shutdownNow()
        executorService.awaitTermination(1, TimeUnit.MINUTES)
    }

    companion object {
        private val logger = Logging.getLogger(GradleModuleService::class.java)

        private fun GradleModule.prettyPrintVersions(versionCatalogBuilder: VersionCatalogBuilder) {
            val first = "-> ${version.group}:${version.name}:${version.version}"
            val rest =
                children.joinToString {
                    "\n----> ${it.version.group}:${it.version.name}"
                }
            logger.lifecycle("Version Catalog: \"${versionCatalogBuilder.name}\" \n$first$rest")
        }

        fun GradleModule.addTo(versionCatalogBuilder: VersionCatalogBuilder) {
            prettyPrintVersions(versionCatalogBuilder)

            versionCatalogBuilder.addTo(version)
            children.forEach {
                versionCatalogBuilder.addTo(it.version)
            }
        }

        private fun VersionCatalogBuilder.addTo(version: GradleModuleVersion) =
            library(version.name, version.group, version.name).withoutVersion()
    }
}
