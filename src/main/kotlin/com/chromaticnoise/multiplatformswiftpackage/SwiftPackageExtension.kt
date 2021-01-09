package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate.TemplateFile
import com.chromaticnoise.multiplatformswiftpackage.dsl.BuildConfigurationDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.DistributionModeDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.PackageTemplateDSL
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl
import groovy.lang.Closure
import org.gradle.api.Action
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
    internal var packageTemplate: SwiftPackageTemplate = SwiftPackageTemplate(file = DEFAULT_TEMPLATE_FILE, configure = {})

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

    /**
     * Builder for the [SwiftPackageTemplate].
     *
     * @param path that points to the template file.
     * @param configure closure that configures the properties of the template.
     */
    public fun packageTemplate(path: File, configure: PackageTemplateDSL.() -> Unit) {
        packageTemplate = SwiftPackageTemplate(
            file = TemplateFile.File(path),
            configure = configure
        )
    }

    public fun packageTemplate(path: File, configure: Closure<PackageTemplateDSL>) {
        packageTemplate(path) { ConfigureUtil.configure(configure, this) }
    }

    public fun packageTemplate(path: File) {
        packageTemplate(path) {}
    }

    public fun packageTemplate(configure: PackageTemplateDSL.() -> Unit) {
        packageTemplate = SwiftPackageTemplate(DEFAULT_TEMPLATE_FILE, configure)
    }

    public fun packageTemplate(configure: Closure<PackageTemplateDSL>) {
        packageTemplate = SwiftPackageTemplate(DEFAULT_TEMPLATE_FILE) { ConfigureUtil.configure(configure, this) }
    }

    private companion object {
        private val DEFAULT_TEMPLATE_FILE =
            TemplateFile.Resource(MultiplatformSwiftPackagePlugin::class.java.getResource("/templates/Package.swift.template"))
    }
}
