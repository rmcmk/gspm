import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlinter)
}

group = "com.github.rmcmk.gspm"
version = "1.0.0-RC15"

plugins.withType<KotlinPluginWrapper> {
    configure<KotlinJvmProjectExtension> {
        jvmToolchain(17)
    }
}

plugins.withType<KotlinDslPlugin> {
    configure<KotlinDslPluginOptions> {
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                // Workaround the kotlin-dsl plugin as it's pinned to Kotlin 1.8 even though the embedded compiler is 1.9
                // @see https://github.com/gradle/gradle/blob/master/platforms/core-configuration/kotlin-dsl-plugins/src/main/kotlin/org/gradle/kotlin/dsl/plugins/dsl/KotlinDslCompilerPlugins.kt#L63
                apiVersion.set(KotlinVersion.KOTLIN_1_9)
                languageVersion.set(KotlinVersion.KOTLIN_1_9)
            }
        }
    }
}

dependencies {
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
