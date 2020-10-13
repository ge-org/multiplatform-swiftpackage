package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.MultiplatformSwiftPackagePlugin
import com.chromaticnoise.multiplatformswiftpackage.task.zipFileName
import org.gradle.api.Project

internal data class SwiftPackageConfiguration(
    private val project: Project,
    private val toolVersion: SwiftToolVersion.Named,
    private val platforms: String,
    private val distributionMode: DistributionMode,
    private val zipChecksum: String
) {

    private val distributionUrl = when (distributionMode) {
        DistributionMode.Local -> null
        is DistributionMode.Remote -> distributionMode.url.appendPath(zipFileName(project))
    }

    internal val templateProperties = mapOf(
        "toolsVersion" to toolVersion.name,
        "name" to project.name,
        "platforms" to platforms,
        "isLocal" to (distributionMode == DistributionMode.Local),
        "url" to distributionUrl?.value,
        "checksum" to zipChecksum.trim()
    )

    internal companion object {
        internal const val FILE_NAME = "Package.swift"

        internal val templateFile =
            MultiplatformSwiftPackagePlugin::class.java.getResource("/templates/Package.swift.template")
    }
}
