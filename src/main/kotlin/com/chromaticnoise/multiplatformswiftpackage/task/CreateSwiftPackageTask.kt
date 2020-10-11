package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import groovy.text.SimpleTemplateEngine
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import java.io.File

internal fun Project.registerCreateSwiftPackageTask() {
    tasks.register("createSwiftPackage") {
        group = "multiplatform-swift-package"
        description = "Creates a Swift package to distribute an XCFramework"

        dependsOn("createXCFramework")
        dependsOn("createZipFile")

        doLast {
            val packageFile = File(extension.outputDirectory.value, SwiftPackageConfiguration.FILE_NAME).apply {
                parentFile.mkdirs()
                createNewFile()
            }

            val toolVersion = (extension.swiftToolsVersion as? SwiftToolVersion.Named) ?:
            throw InvalidUserDataException("""
             |Swift tools version is not set.
             |Use the following function to set it in your build configuration:
             |swiftToolsVersion("5.3")
            """.trimMargin())

            val configuration = SwiftPackageConfiguration(
                project = project,
                toolVersion = toolVersion,
                platforms = platforms(extension),
                distributionMode = extension.distributionMode,
                zipChecksum = zipFileChecksum(project, extension.outputDirectory)
            )

            SimpleTemplateEngine()
                .createTemplate(SwiftPackageConfiguration.templateFile)
                .make(configuration.templateProperties)
                .writeTo(packageFile.writer())
        }
    }
}

private fun platforms(extension: ExtensionConfiguration): String = extension.targetPlatforms.flatMap { platform ->
    extension.appleTargets
        .filter { appleTarget -> platform.targets.contains(TargetName(appleTarget.nativeTarget.name)) }
        .mapNotNull { target -> target.nativeTarget.konanTarget.family.swiftPackagePlatformName }
        .distinct()
        .map { platformName -> ".$platformName(.v${platform.version.name})" }
}.joinToString(",\n")
