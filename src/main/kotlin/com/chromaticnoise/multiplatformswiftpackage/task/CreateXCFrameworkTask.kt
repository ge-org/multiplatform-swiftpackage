package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.extension
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import java.io.File

internal fun Project.registerCreateXCFrameworkTask() = tasks.register("createXCFramework", Exec::class.java) {
    group = "multiplatform-swift-package"
    description = "Creates an XCFramework for all declared Apple targets"

    val buildConfiguration = extension.buildConfiguration
    val xcFrameworkDestination = File(extension.outputDirectory.value, "${project.name}.xcframework")

    val frameworks = extension.appleTargets.mapNotNull { target ->
        target.framework(buildConfiguration)
    }

    dependsOn(frameworks.map { it.linkTaskName })

    executable = "xcodebuild"
    args(mutableListOf<String>().apply {
        add("-create-xcframework")
        add("-output")
        add(xcFrameworkDestination.path)
        frameworks.forEach { framework ->
            add("-framework")
            add(framework.outputFile.path)
            add("-debug-symbols")
            add(File(framework.outputFile.parent, "${project.name}.framework.dSYM").path)
        }
    })

    doFirst {
        xcFrameworkDestination.deleteRecursively()
    }
}
