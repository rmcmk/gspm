package dev.rmcmk.gspm.module

import dev.rmcmk.git.SubmoduleDefinition
import dev.rmcmk.gradle.gmvmb.GradleModule
import java.io.ByteArrayOutputStream

data class GradleModuleResult(
    val module: GradleModule,
    val definition: SubmoduleDefinition,
    private val output: String,
    private val error: String,
) {
    companion object {
        fun create(
            module: GradleModule,
            definition: SubmoduleDefinition,
            stdout: ByteArrayOutputStream,
            stderr: ByteArrayOutputStream,
        ): GradleModuleResult {
            val output = stdout.toString(Charsets.UTF_8)
            val error = stderr.toString(Charsets.UTF_8)
            if (error.isNotEmpty()) {
                throw IllegalStateException("Error occurred while processing ${definition.path}: $error")
            }
            return GradleModuleResult(
                module,
                definition,
                output,
                error,
            )
        }
    }
}
