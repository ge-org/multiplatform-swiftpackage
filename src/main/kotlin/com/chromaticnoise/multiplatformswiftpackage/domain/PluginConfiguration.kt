package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.SwiftPackageExtension
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.*

internal class PluginConfiguration private constructor(
    val buildConfiguration: BuildConfiguration,
    val outputDirectory: OutputDirectory,
    val swiftToolsVersion: SwiftToolVersion,
    val distributionMode: DistributionMode,
    val targetPlatforms: Collection<TargetPlatform>,
    val appleTargets: Collection<AppleTarget>
) {
    internal companion object {
        fun of(extension: SwiftPackageExtension): Either<List<PluginConfigurationError>, PluginConfiguration> {
            val errors = mutableListOf<PluginConfigurationError>().apply {
                if (extension.swiftToolsVersion == null) {
                    add(MissingSwiftToolsVersion)
                }

                if (extension.targetPlatforms.isEmpty()) {
                    add(MissingTargetPlatforms)
                }

                if (extension.appleTargets.isEmpty() && extension.targetPlatforms.isNotEmpty()) {
                    add(MissingAppleTargets)
                }
            }

            return if (errors.isEmpty()) {
                Either.Right(
                    PluginConfiguration(
                        extension.buildConfiguration,
                        extension.outputDirectory,
                        extension.swiftToolsVersion!!,
                        extension.distributionMode,
                        extension.targetPlatforms,
                        extension.appleTargets
                    )
                )
            } else {
                Either.Left(errors)
            }
        }
    }

    internal enum class PluginConfigurationError {
        MissingSwiftToolsVersion,
        MissingTargetPlatforms,
        MissingAppleTargets
    }
}
