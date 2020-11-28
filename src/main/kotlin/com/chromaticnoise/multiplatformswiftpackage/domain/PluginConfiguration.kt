package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.SwiftPackageExtension
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.*

internal class PluginConfiguration private constructor(
    val buildConfiguration: BuildConfiguration,
    val packageName: PackageName,
    val outputDirectory: OutputDirectory,
    val swiftToolsVersion: SwiftToolVersion,
    val distributionMode: DistributionMode,
    val targetPlatforms: Collection<TargetPlatform>,
    val appleTargets: Collection<AppleTarget>
) {
    internal companion object {
        fun of(extension: SwiftPackageExtension): Either<List<PluginConfigurationError>, PluginConfiguration> {
            val targetPlatforms = extension.targetPlatforms.platforms
            val packageName = extension.getPackageName()

            val errors = mutableListOf<PluginConfigurationError>().apply {
                if (extension.swiftToolsVersion == null) {
                    add(MissingSwiftToolsVersion)
                }

                val targetPlatformErrors = extension.targetPlatforms.errors
                if (targetPlatformErrors.isNotEmpty()) {
                    addAll(targetPlatformErrors)
                }

                if (targetPlatformErrors.isEmpty() && targetPlatforms.isEmpty()) {
                    add(MissingTargetPlatforms)
                }

                if (extension.appleTargets.isEmpty() && targetPlatforms.isNotEmpty()) {
                    add(MissingAppleTargets)
                }

                packageName.leftValueOrNull?.let { error -> add(error) }
            }

            return if (errors.isEmpty()) {
                Either.Right(
                    PluginConfiguration(
                        extension.buildConfiguration,
                        packageName.orNull!!,
                        extension.outputDirectory,
                        extension.swiftToolsVersion!!,
                        extension.distributionMode,
                        targetPlatforms,
                        extension.appleTargets
                    )
                )
            } else {
                Either.Left(errors)
            }
        }

        private fun SwiftPackageExtension.getPackageName(): Either<PluginConfigurationError, PackageName> = packageName
            ?: appleTargets.map { it.getFramework(buildConfiguration) }.firstOrNull()?.let { framework ->
                PackageName.of(framework.name.value)
            } ?: Either.Left(InvalidPackageName(null))
    }

    internal sealed class PluginConfigurationError {
        object MissingSwiftToolsVersion : PluginConfigurationError()
        data class InvalidTargetName(val name: String) : PluginConfigurationError()
        object MissingTargetPlatforms : PluginConfigurationError()
        object MissingAppleTargets : PluginConfigurationError()
        data class InvalidPackageName(val name: String?) : PluginConfigurationError()
    }
}
