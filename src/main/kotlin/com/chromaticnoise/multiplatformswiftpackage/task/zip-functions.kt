package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.OutputDirectory
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.io.File

internal fun zipFileChecksum(project: Project, outputDirectory: OutputDirectory): String {
    val outputPath = outputDirectory.value
    return File(outputPath, zipFileName(project))
        .takeIf { it.exists() }
        ?.let { zipFile ->
            ByteArrayOutputStream().use { os ->
                project.exec {
                    workingDir = outputPath
                    executable = "swift"
                    args = listOf("package", "compute-checksum", zipFile.name)
                    standardOutput = os
                }
                os.toString()
            }
        } ?: ""
}

internal fun zipFileName(project: Project) = "${project.name}-${project.version}.zip"
