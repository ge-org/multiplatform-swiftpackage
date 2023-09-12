@file:Suppress("UnstableApiUsage")
pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            gradlePluginPortal()
            mavenCentral()
        }
    }
}

rootProject.name = "multiplatform-swiftpackage"
