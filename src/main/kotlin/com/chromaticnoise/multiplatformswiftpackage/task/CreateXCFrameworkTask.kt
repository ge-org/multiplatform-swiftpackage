package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import java.io.File

internal fun Project.registerCreateXCFrameworkTask() = tasks.register("createXCFramework", Exec::class.java) {
    group = "multiplatform-swift-package"
    description = "Creates an XCFramework for all declared Apple targets"

    val configuration = getConfigurationOrThrow()
    val buildConfiguration = configuration.buildConfiguration
    val xcFrameworkDestination = File(configuration.outputDirectory.value, "${project.name}.xcframework")

    val frameworks = configuration.appleTargets.mapNotNull { target ->
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

            val dsymFile = File(framework.outputFile.parent, "${project.name}.framework.dSYM")
            if (dsymFile.exists()) {
                add("-debug-symbols")
                add(dsymFile.path)
            }
        }
    })

    doFirst {
        xcFrameworkDestination.deleteRecursively()
    }
}
