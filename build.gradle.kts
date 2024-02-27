import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm") version "1.9.22"
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlinter)
}

group = "com.github.rmcmk.gspm"
version = "1.0.0-RC15"

plugins.withType<KotlinPluginWrapper> {
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(libs.java.ini.parser)
    implementation(libs.gmvmb)
}

@Suppress("UnstableApiUsage") gradlePlugin {
    website = "https://rmcmk.dev/gspm"
    vcsUrl = "https://github.com/rmcmk/gspm.git"

    plugins {
        create("gspm") {
            displayName = "GSPM - Gradle Submodule Package Manager"
            description = """
				Treats Git Submodules as first class citizens in a Gradle build.
				Provides a version catalog for submodules and a way to manage them.
			""".trimIndent()
            id = "gspm"
            tags = setOf("submodules", "version catalog", "git", "package manager")
            implementationClass = "dev.rmcmk.gspm.GspmPlugin"
        }
    }
}

// com.github.sya-ri:kgit:1.0.6
// https://docs.nokee.dev/manual/plugin-references.html#sec:plugin-reference-gradledev
