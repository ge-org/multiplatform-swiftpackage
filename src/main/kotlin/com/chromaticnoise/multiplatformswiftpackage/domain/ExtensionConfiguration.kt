package com.chromaticnoise.multiplatformswiftpackage.domain

internal data class ExtensionConfiguration(
    val buildConfiguration: BuildConfiguration,
    val outputDirectory: OutputDirectory,
    val swiftToolsVersion: SwiftToolVersion,
    val distributionMode: DistributionMode,
    val targetPlatforms: Collection<TargetPlatform>,
    val appleTargets: Collection<AppleTarget>
)
