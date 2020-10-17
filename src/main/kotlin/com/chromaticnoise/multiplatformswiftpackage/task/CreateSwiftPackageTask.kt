package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project
import java.io.File

internal fun Project.registerCreateSwiftPackageTask() {
    tasks.register("createSwiftPackage") {
        group = "multiplatform-swift-package"
        description = "Creates a Swift package to distribute an XCFramework"

        dependsOn("createXCFramework")
        dependsOn("createZipFile")

        doLast {
            val configuration = getConfigurationOrThrow()
            val packageFile = File(configuration.outputDirectory.value, SwiftPackageConfiguration.FILE_NAME).apply {
                parentFile.mkdirs()
                createNewFile()
            }

            val packageConfiguration = SwiftPackageConfiguration(
                project = project,
                toolVersion = configuration.swiftToolsVersion,
                platforms = platforms(configuration),
                distributionMode = configuration.distributionMode,
                zipChecksum = zipFileChecksum(project, configuration.outputDirectory)
            )

            SimpleTemplateEngine()
                .createTemplate(SwiftPackageConfiguration.templateFile)
                .make(packageConfiguration.templateProperties)
                .writeTo(packageFile.writer())
        }
    }
}

private fun platforms(configuration: PluginConfiguration): String = configuration.targetPlatforms.flatMap { platform ->
    configuration.appleTargets
        .filter { appleTarget -> platform.targets.contains(TargetName.of(appleTarget.nativeTarget.name)) }
        .mapNotNull { target -> target.nativeTarget.konanTarget.family.swiftPackagePlatformName }
        .distinct()
        .map { platformName -> ".$platformName(.v${platform.version.name})" }
}.joinToString(",\n")
