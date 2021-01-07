package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.dsl.BuildConfigurationDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.DistributionModeDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl
import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import java.io.File

public open class SwiftPackageExtension(project: Project) {

    internal var buildConfiguration: BuildConfiguration = BuildConfiguration.Release
    internal var packageName: Either<PluginConfigurationError, PackageName>? = null
    internal var outputDirectory: OutputDirectory = OutputDirectory(File(project.projectDir, "swiftpackage"))
    internal var swiftToolsVersion: SwiftToolVersion? = null
    internal var distributionMode: DistributionMode = DistributionMode.Local
    internal var targetPlatforms: Collection<Either<List<PluginConfigurationError>, TargetPlatform>> = emptyList()
    internal var appleTargets: Collection<AppleTarget> = emptyList()

    /**
     * Sets the name of the Swift package.
     * Defaults to the base name of the first framework found in the project.
     *
     * @param name of the Swift package.
     */
    public fun packageName(name: String) {
        packageName = PackageName.of(name)
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
    public fun buildConfiguration(configure: BuildConfigurationDSL.() -> Unit) {
        BuildConfigurationDSL().also { dsl ->
            dsl.configure()
            buildConfiguration = dsl.buildConfiguration
        }
    }

    public fun buildConfiguration(configure: Closure<BuildConfigurationDSL>) {
        buildConfiguration { ConfigureUtil.configure(configure, this) }
    }

    /**
     * Builder for the [DistributionMode].
     */
    public fun distributionMode(configure: DistributionModeDSL.() -> Unit) {
        DistributionModeDSL().also { dsl ->
            dsl.configure()
            distributionMode = dsl.distributionMode
        }
    }

    public fun distributionMode(configure: Closure<DistributionModeDSL>) {
        distributionMode { ConfigureUtil.configure(configure, this) }
    }

    /**
     * Builder for instances of [TargetPlatform].
     */
    public fun targetPlatforms(configure: TargetPlatformDsl.() -> Unit) {
        TargetPlatformDsl().also { dsl ->
            dsl.configure()
            targetPlatforms = dsl.targetPlatforms
        }
    }

    public fun targetPlatforms(configure: Closure<TargetPlatformDsl>) {
        targetPlatforms { ConfigureUtil.configure(configure, this) }
    }
}
