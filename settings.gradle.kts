pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()

        maven("https://maven.fabricmc.net/")
        maven("https://maven.ornithemc.net/releases")

		maven("https://maven.kikugie.dev/snapshots")
		maven("https://maven.kikugie.dev/releases")
        maven("https://repo.polyfrost.cc/releases")
    }
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.3"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"
	create(rootProject) {
        fun mc(mcVersion: String, loaders: Iterable<String>) {
            for (loader in loaders) {
                version("$mcVersion-$loader", mcVersion)
            }
        }

		mc("1.8.9", listOf("ornithe"))

		vcsVersion = "1.8.9-ornithe"
	}
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "Skyboxify"
