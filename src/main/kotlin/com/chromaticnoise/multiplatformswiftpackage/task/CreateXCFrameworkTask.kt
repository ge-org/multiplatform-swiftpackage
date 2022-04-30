package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.task
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import java.io.File


internal fun getMacosFrameworks(configuration: PluginConfiguration): List<AppleFramework> {
    return configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }
        .filter {
            it.linkTask.name.contains("MacosX64") || it.linkTask.name.contains("MacosArm64")
        }
}

internal fun getIosSimulatorFrameworks(configuration: PluginConfiguration): List<AppleFramework> {
    return configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }
        .filter {
            it.linkTask.name.contains("IosX64") || it.linkTask.name.contains("IosSimulatorArm64")
        }
}

internal fun getWatchosSimulatorFrameworks(configuration: PluginConfiguration): List<AppleFramework> {
    return configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }
        .filter {
            it.linkTask.name.contains("watchosX86")
                    || it.linkTask.name.contains("watchosX64")
                    || it.linkTask.name.contains("watchosSimulatorArm64")
        }
}

internal fun getTvosSimulatorFrameworks(configuration: PluginConfiguration): List<AppleFramework> {
    return configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }
        .filter {
            it.linkTask.name.contains("tvosX64") || it.linkTask.name.contains("tvosSimulatorArm64")
        }
}

internal fun Project.registerCreateUniversalMacosFrameworkTask() =
    task("createUniversalMacosFramework") {
        // Can't use FatFrameworkTask 🙃
        // https://youtrack.jetbrains.com/issue/KT-47355/Support-macos-target-for-FatFramework-task
        // workaround:
        // https://gist.github.com/JUSTINMKAUFMAN/6627c3c8571563b36efc9c832f6fa2b1
        group = "multiplatform-swift-package"
        description = "Creates a universal (fat) macos framework"
        val configuration = getConfigurationOrThrow()
        onlyIf { getMacosFrameworks(configuration).size > 1 }
        val targets = getMacosFrameworks(configuration)
        dependsOn(targets.map { it.linkTask.name })
        doLast {
            if (targets.isNotEmpty()) {
                delete(buildDir.resolve("bin/macosUniversal"))
                val buildType = if (targets[0].linkTask.name.contains("Release")) "release" else "debug"
                val frameworkName = targets[0].name.value
                val destinationDir = buildDir.resolve("bin/macosUniversal/${buildType}Framework")
                mkdir(destinationDir.parent)
                mkdir(destinationDir)
                val tempUniversalBinaryLocation = File(destinationDir, frameworkName)
                exec {
                    commandLine(
                        "lipo",
                        "-create",
                        "${targets[0].outputFile.path}/Versions/A/${frameworkName}",
                        "${targets[1].outputFile.path}/Versions/A/${frameworkName}",
                        "-output",
                        tempUniversalBinaryLocation.path
                    )
                }
                val aFramework = targets[0].outputFile.path
                copy {
                    from(aFramework)
                    into(File(destinationDir, "$frameworkName.framework"))
                }
                // delete the old mono framework binary we copied here
                val binaryFinalLocation = File(destinationDir, "${frameworkName}.framework/Versions/A/$frameworkName")
                val binaryFinalSymlinkLocation = File(destinationDir, "${frameworkName}.framework/$frameworkName")
                delete(binaryFinalLocation)
                delete(binaryFinalSymlinkLocation)
                // copy new universal binary to binaryFinalSymlinkLocation
                copy {
                    from(tempUniversalBinaryLocation)
                    into(File(destinationDir, "$frameworkName.framework/Versions/A"))
                }
                delete(tempUniversalBinaryLocation)
                exec {
                    //recreate the symlink normally to the binary normally in framework folder
                    commandLine(
                        "ln",
                        "-s",
                        binaryFinalLocation.toPath(),
                        binaryFinalSymlinkLocation.toPath()
                    )
                }
            }
        }
    }

internal fun Project.registerCreateUniversalIosSimulatorFrameworkTask() =
    task<FatFrameworkTask>("createUniversalIosSimulatorFramework") {
        group = "multiplatform-swift-package"
        description = "Creates a universal (fat) ios simulator framework"
        val configuration = getConfigurationOrThrow()
        onlyIf { getIosSimulatorFrameworks(configuration).size > 1 }
        val targets = getIosSimulatorFrameworks(configuration)
        dependsOn(targets.map { it.linkTask.name })
        if (targets.isNotEmpty()) {
            val buildType = if (targets[0].linkTask.name.contains("Release")) "release" else "debug"
            baseName = configuration.packageName.value
            destinationDir = buildDir.resolve("bin/iosSimulatorUniversal/${buildType}Framework")
            from(targets.mapNotNull { it.framework })
        }
    }

internal fun Project.registerCreateUniversalWatchosSimulatorFrameworkTask() =
    tasks.register("createUniversalWatchosSimulatorFramework", FatFrameworkTask::class.java) {
        group = "multiplatform-swift-package"
        description = "Creates a universal (fat) watchos simulator framework"
        val configuration = getConfigurationOrThrow()
        onlyIf { getWatchosSimulatorFrameworks(configuration).size > 1 }
        val targets = getWatchosSimulatorFrameworks(configuration)
        dependsOn(targets.map { it.linkTask.name })
        if (targets.isNotEmpty()) {
            val buildType = if (targets[0].linkTask.name.contains("Release")) "release" else "debug"
            baseName = configuration.packageName.value
            destinationDir = buildDir.resolve("bin/watchosSimulatorUniversal/$buildType")
            from(targets.mapNotNull { it.framework })
        }
    }

internal fun Project.registerCreateUniversalTvosSimulatorFrameworkTask() =
    tasks.register("createUniversalTvosSimulatorFramework", FatFrameworkTask::class.java) {
        group = "multiplatform-swift-package"
        description = "Creates a universal (fat) tvos simulator framework"
        val configuration = getConfigurationOrThrow()
        onlyIf { getTvosSimulatorFrameworks(configuration).size > 1 }
        val targets = getTvosSimulatorFrameworks(configuration)
        dependsOn(targets.map { it.linkTask.name })
        if (targets.isNotEmpty()) {
            val buildType = if (targets[0].linkTask.name.contains("Release")) "release" else "debug"
            baseName = configuration.packageName.value
            destinationDir = buildDir.resolve("bin/tvosSimulatorUniversal/$buildType")
            from(targets.mapNotNull { it.framework })
        }
    }


internal fun removeMonoFrameworksAndAddUniversalFrameworkIfNeeded(
    binFolderPrefix: String,
    buildDir: File,
    monoFrameworks: List<AppleFramework>,
    outputFrameworks: MutableList<AppleFramework>
) {
    if (monoFrameworks.size > 1) {
        monoFrameworks.forEach { mono ->
            outputFrameworks.removeIf { mono.outputFile == it.outputFile }
        }
        val frameworkName = monoFrameworks[0].name
        val buildType = if (monoFrameworks[0].linkTask.name.contains("Release")) "release" else "debug"
        val destinationDir = buildDir.resolve("bin/${binFolderPrefix}Universal/${buildType}Framework")
        val outputFile = AppleFrameworkOutputFile(File(destinationDir, "${frameworkName.value}.framework"))
        outputFrameworks.add(
            AppleFramework(
                outputFile,
                frameworkName,
                AppleFrameworkLinkTask("")
            )
        )
    }
}


internal fun Project.registerCreateXCFrameworkTask() = tasks.register("createXCFramework", Exec::class.java) {
    group = "multiplatform-swift-package"
    description = "Creates an XCFramework for all declared Apple targets"

    val configuration = getConfigurationOrThrow()
    val xcFrameworkDestination =
        File(configuration.outputDirectory.value, "${configuration.packageName.value}.xcframework")
    val outputFrameworks =
        configuration.appleTargets.mapNotNull { it.getFramework(configuration.buildConfiguration) }.toMutableList()

    dependsOn(outputFrameworks.map { it.linkTask.name })
    dependsOn("createUniversalMacosFramework")
    dependsOn("createUniversalIosSimulatorFramework")
    dependsOn("createUniversalWatchosSimulatorFramework")
    dependsOn("createUniversalTvosSimulatorFramework")

    val macosFrameworks = getMacosFrameworks(configuration)
    removeMonoFrameworksAndAddUniversalFrameworkIfNeeded(
        "macos",
        buildDir,
        macosFrameworks,
        outputFrameworks
    )
    val iosSimulatorFrameworks = getIosSimulatorFrameworks(configuration)
    removeMonoFrameworksAndAddUniversalFrameworkIfNeeded(
        "iosSimulator",
        buildDir,
        iosSimulatorFrameworks,
        outputFrameworks
    )
    val watchosSimulatorFrameworks = getWatchosSimulatorFrameworks(configuration)
    removeMonoFrameworksAndAddUniversalFrameworkIfNeeded(
        "watchosSimulator",
        buildDir,
        watchosSimulatorFrameworks,
        outputFrameworks
    )
    val tvosSimulatorFrameworks = getTvosSimulatorFrameworks(configuration)
    removeMonoFrameworksAndAddUniversalFrameworkIfNeeded(
        "tvosSimulator",
        buildDir,
        tvosSimulatorFrameworks,
        outputFrameworks
    )


    executable = "xcodebuild"
    args(mutableListOf<String>().apply {
        add("-create-xcframework")
        add("-output")
        add(xcFrameworkDestination.path)
        outputFrameworks.forEach { framework ->
            add("-framework")
            add(framework.outputFile.path)

            framework.dsymFile.takeIf { it.exists() }?.let { dsymFile ->
                add("-debug-symbols")
                add(dsymFile.absolutePath)
            }
        }
    })

    doFirst {
        xcFrameworkDestination.deleteRecursively()
    }
}
