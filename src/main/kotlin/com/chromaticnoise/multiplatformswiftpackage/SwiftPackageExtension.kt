package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.dsl.BuildConfigurationDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.DistributionModeDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File

public open class SwiftPackageExtension(project: Project) {

    internal var buildConfiguration: BuildConfiguration = BuildConfiguration.Release
    internal var frameworkName: FrameworkName = FrameworkName(project.name)
    internal var outputDirectory: OutputDirectory = OutputDirectory(File(project.projectDir, "swiftpackage"))
    internal var swiftToolsVersion: SwiftToolVersion? = null
    internal var distributionMode: DistributionMode = DistributionMode.Local
    internal var targetPlatforms: Collection<Either<List<PluginConfigurationError>, TargetPlatform>> = emptyList()
    internal var appleTargets: Collection<AppleTarget> = emptyList()

    /**
     * Sets the name of the framework to be built.
     * Defaults to ${project.name}
     *
     * @param name of the framework to generate.
     */
    public fun frameworkName(name: String) {
        frameworkName = FrameworkName(name)
    }

    /**
     * Sets the directory where files like the Package.swift and XCFramework will be created.
     * Defaults to $projectDir/swiftpackage
     *
     * @param directory where the files will be created.
     */
    public fun outputDirectory(directory: File) {
        outputDirectory = OutputDirectory((directory))
    }

    /**
     * Version of the Swift tools. That's the version added to the Package.swift header.
     * E.g. 5.3
     */
    public fun swiftToolsVersion(name: String) {
        swiftToolsVersion = SwiftToolVersion.of(name)
    }

    /**
     * Builder for the [BuildConfiguration].
     */
    public fun buildConfiguration(builder: Action<BuildConfigurationDSL>) {
        builder.build(BuildConfigurationDSL()) { dsl ->
            buildConfiguration = dsl.buildConfiguration
        }
    }

    /**
     * Builder for the [DistributionMode].
     */
    public fun distributionMode(builder: Action<DistributionModeDSL>) {
        builder.build(DistributionModeDSL()) { dsl ->
            distributionMode = dsl.distributionMode
        }
    }

    /**
     * Builder for instances of [TargetPlatform].
     */
    public fun targetPlatforms(builder: Action<TargetPlatformDsl>) {
        builder.build(TargetPlatformDsl()) { dsl ->
            targetPlatforms = dsl.targetPlatforms
        }
    }
}
