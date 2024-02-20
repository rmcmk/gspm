import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	`kotlin-dsl`
	alias(libs.plugins.gradle.publish)
	alias(libs.plugins.kotlinter)
}

group = "com.github.rmcmk.gspm"
version = "1.0.0-RC9"

dependencies {
	implementation(libs.java.ini.parser)
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
			implementationClass = "dev.rmcmk.gspm.gradle.GspmPlugin"
		}
	}
}


tasks.register<WriteProperties>("generateCoordinateProperties") {
	sourceSets.main {
		destinationFile = output.resourcesDir?.resolve("coordinate.properties")
			?: error("No resources directory found.")
	}

	doFirst {
		property("name", project.name)
		property("group", project.group)
		property("version", project.version)
	}
}

tasks.withType<KotlinCompile> {
	dependsOn("generateCoordinateProperties")
}
