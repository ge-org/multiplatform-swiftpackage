package com.chromaticnoise.multiplatformswiftpackage.domain

import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBinary
import java.io.File

internal class AppleFramework(
    val outputFile: AppleFrameworkOutputFile,
    val name: AppleFrameworkName,
    val linkTask: AppleFrameworkLinkTask
) {

    val dsymFile: File get() = File(outputFile.parent, "${name.value}.framework.dSYM")

    internal companion object
}

internal fun AppleFramework.Companion.of(binary: NativeBinary?): AppleFramework? = binary?.let {
    AppleFramework(
        AppleFrameworkOutputFile(it.outputFile),
        AppleFrameworkName(it.baseName),
        AppleFrameworkLinkTask(it.linkTaskName)
    )
}

internal data class AppleFrameworkOutputFile(private val file: File) {
    val path: String get() = file.path

    val parent: File get() = file.parentFile
}

internal data class AppleFrameworkName(val value: String)

internal data class AppleFrameworkLinkTask(val name: String)
