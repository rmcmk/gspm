package dev.rmcmk.gspm.resource

import dev.rmcmk.gradle.gmvmb.GmvmbPlugin
import java.io.File

class InitScript(override val file: File) : Resource() {
    override val fileName = "init.gradle.kts"
    override val content =
        """
        /**
         * =================================================================================
         * THIS FILE COMES FROM THE GSPM GRADLE PLUGIN. DO NOT MODIFY DIRECTLY.
         * =================================================================================
         */
        import ${GMVMB_PLUGIN_CLASS.qualifiedName}

        initscript {
            repositories {
                mavenCentral()
                maven("https://jitpack.io")
            }
            dependencies { classpath("com.github.rmcmk.gmvmb:gmvmb:1.0.0-RC1") }
        }

        allprojects { apply<${GMVMB_PLUGIN_CLASS.simpleName}>() }
        """.trimIndent()

    companion object {
        private val GMVMB_PLUGIN_CLASS = GmvmbPlugin::class
    }
}
