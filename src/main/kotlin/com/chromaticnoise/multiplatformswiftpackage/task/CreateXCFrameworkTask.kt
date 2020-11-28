package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import java.io.File

internal fun Project.registerCreateXCFrameworkTask() = tasks.register("createXCFramework", Exec::class.java) {
    group = "multiplatform-swift-package"
    description = "Creates an XCFramework for all declared Apple targets"

    val configuration = getConfigurationOrThrow()
    val xcFrameworkDestination = File(configuration.outputDirectory.value, "${configuration.packageName.value}.xcframework")
    val frameworks = configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }

    dependsOn(frameworks.map { it.linkTask.name })

    executable = "xcodebuild"
    args(mutableListOf<String>().apply {
        add("-create-xcframework")
        add("-output")
        add(xcFrameworkDestination.path)
        frameworks.forEach { framework ->
            add("-framework")
            add(framework.outputFile.path)

            framework.dsymFile.takeIf { it.exists() }?.let { dsymFile ->
                add("-debug-symbols")
                add(dsymFile.path)
            }
        }
    })

    doFirst {
        xcFrameworkDestination.deleteRecursively()
    }
}
