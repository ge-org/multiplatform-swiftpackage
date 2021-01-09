package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate.TemplateFile
import com.chromaticnoise.multiplatformswiftpackage.dsl.PackageTemplateDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.Platform
import com.chromaticnoise.multiplatformswiftpackage.dsl.RemoteDistribution
import com.chromaticnoise.multiplatformswiftpackage.dsl.TemplateKey
import com.chromaticnoise.multiplatformswiftpackage.dsl.TemplateKey.*
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

    internal val templateProperties: Map<String, Any?> get() = PackageTemplateDSL(
        toolsVersion = toolsVersion.name,
        packageName = packageName.value,
        platforms = platforms,
        remoteDistribution = remoteDistribution
    ).run {
        packageTemplate.configure(this)
        mapOf(
            ToolsVersion to toolsVersion,
            Name to packageName,
            Platforms to platforms.joinToString(",\n") { ".${it.name}(.v${it.version})" },
            IsLocal to (remoteDistribution == null),
            Url to remoteDistribution?.fullUrl,
            Checksum to remoteDistribution?.zipFileChecksum
        )
            .withStringKeys()
            .apply { putAll(extraProperties) }
    }

    private val remoteDistribution get() = (distributionMode as? DistributionMode.Remote)?.let { mode ->
        RemoteDistribution(
            baseUrl = mode.url.value,
            zipFileName = zipFileName(project, packageName),
            zipFileChecksum = zipFileChecksum(project, outputDirectory, packageName).trim()
        )
    }

    private val platforms: MutableCollection<Platform> get() = targetPlatforms.flatMap { platform ->
        appleTargets
            .filter { appleTarget -> platform.targets.firstOrNull { it.konanTarget == appleTarget.nativeTarget.konanTarget } != null }
            .mapNotNull { target -> target.nativeTarget.konanTarget.family.swiftPackagePlatformName }
            .distinct()
            .map { platformName -> Platform(platformName, platform.version.name) }
    }.toMutableList()

    private fun Map<TemplateKey, Any?>.withStringKeys(): MutableMap<String, Any?> = mapKeys { it.key.value }.toMutableMap()

    internal companion object {
        internal const val FILE_NAME = "Package.swift"
    }
}
