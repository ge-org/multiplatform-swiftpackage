package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.dsl.BuildConfigurationDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.DistributionModeDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File

public open class SwiftPackageExtension(project: Project) {

    internal var container = ExtensionConfiguration(
        buildConfiguration = BuildConfiguration.Release,
        outputDirectory = OutputDirectory(File(project.projectDir, "swiftpackage")),
        swiftToolsVersion = SwiftToolVersion.Unknown,
        distributionMode = DistributionMode.Local,
        targetPlatforms = emptyList(),
        appleTargets = emptyList()
    )

    /**
     * Sets the directory where files like the Package.swift and XCFramework will be created.
     * Defaults to $projectDir/swiftpackage
     *
     * @param directory where the files will be created.
     */
    public fun outputDirectory(directory: File) {
        container = container.copy(outputDirectory = OutputDirectory((directory)))
    }

    /**
     * Version of the Swift tools. That's the version added to the Package.swift header.
     * E.g. 5.3
     */
    public fun swiftToolsVersion(name: String) {
        container = container.copy(swiftToolsVersion = SwiftToolVersion.Named(name))
    }

    /**
     * Builder for the [BuildConfiguration].
     */
    public fun buildConfiguration(builder: Action<BuildConfigurationDSL>) {
        builder.build(BuildConfigurationDSL()) { dsl ->
            container = container.copy(buildConfiguration = dsl.buildConfiguration)
        }
    }

    /**
     * Builder for the [DistributionMode].
     */
    public fun distributionMode(builder: Action<DistributionModeDSL>) {
        builder.build(DistributionModeDSL()) { dsl ->
            container = container.copy(distributionMode = dsl.distributionMode)
        }
    }

    /**
     * Builder for instances of [TargetPlatform].
     */
    public fun targetPlatforms(builder: Action<TargetPlatformDsl>) {
        builder.build(TargetPlatformDsl()) { dsl ->
            container = container.copy(targetPlatforms = dsl.targetPlatforms)
        }
    }
}
