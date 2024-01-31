plugins {
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlinter)
}

group = "dev.rmcmk.gspm"
version = "1.0.0-RC2"

dependencies {
    implementation(libs.java.ini.parser)
}

@Suppress("UnstableApiUsage") gradlePlugin {
    website = "https://rmcmk.dev/gspm"
    vcsUrl = "https://github.com/rmcmk/gspm.git"

    plugins {
        create("gspm") {
            displayName = "Gradle Submodule Package Manager"
            description =
                "Treats Git Submodules as first class citizens in a Gradle build. Provides a version catalog for submodules and a way to manage them."
            id = "gspm"
            tags = setOf("submodules", "version catalog", "git", "package manager")
            implementationClass = "dev.rmcmk.gspm.gradle.GspmPlugin"
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        pom {
            packaging = "jar"
            name.set("GSPM - Gradle Submodule Package Manager")
            description.set(
                """
                    Treats Git Submodules as first class citizens in a Gradle build.
                    Provides a version catalog for submodules and a way to manage them.
                """.trimIndent()
            )

            url.set("https://rmcmk.dev/gspm")
            inceptionYear.set("2024")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            scm {
                connection.set("scm:git:https://github.com/rmcmk/gspm.git")
                developerConnection.set("scm:git:git@git.github.com:rmcmk/gspm.git")
                url.set("https://git.github.com/rmcmk/gspm")
            }

            issueManagement {
                system.set("GitHub")
                url.set("https://git.github.com/rmcmk/gspm")
            }
        }
    }
}
