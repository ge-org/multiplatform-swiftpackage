package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate.TemplateFile
import com.chromaticnoise.multiplatformswiftpackage.task.zipFileChecksum
import com.chromaticnoise.multiplatformswiftpackage.task.zipFileName
import org.gradle.api.Project

internal data class SwiftPackageConfiguration(
    private val project: Project,
    private val packageTemplate: SwiftPackageTemplate,
    private val toolsVersion: SwiftToolVersion,
    private val packageName: PackageName,
    private val distributionMode: DistributionMode,
    private val outputDirectory: OutputDirectory,
    private val targetPlatforms: Collection<TargetPlatform>,
    private val appleTargets: Collection<AppleTarget>
) {

    internal val templateFile: TemplateFile get() = packageTemplate.file

    internal val templateProperties: Map<String, Any?> get() = mutableMapOf<String, Any?>(
        "toolsVersion" to toolsVersion.name,
        "name" to packageName.value,
        "platforms" to platforms,
        "isLocal" to (distributionMode == DistributionMode.Local),
        "url" to distributionUrl?.value,
        "checksum" to zipFileChecksum(project, outputDirectory, packageName).trim()
    ).apply { putAll(packageTemplate.properties) }

    private val distributionUrl get() = when (distributionMode) {
        DistributionMode.Local -> null
        is DistributionMode.Remote -> distributionMode.url.appendPath(zipFileName(project, packageName))
    }

    private val platforms: String get() = targetPlatforms.flatMap { platform ->
        appleTargets
            .filter { appleTarget -> platform.targets.firstOrNull { it.konanTarget == appleTarget.nativeTarget.konanTarget } != null }
            .mapNotNull { target -> target.nativeTarget.konanTarget.family.swiftPackagePlatformName }
            .distinct()
            .map { platformName -> ".$platformName(.v${platform.version.name})" }
    }.joinToString(",\n")

    internal companion object {
        internal const val FILE_NAME = "Package.swift"
    }
}
