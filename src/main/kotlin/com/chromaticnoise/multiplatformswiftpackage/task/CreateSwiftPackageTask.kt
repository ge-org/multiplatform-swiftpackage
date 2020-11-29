package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate.TemplateFile
import com.chromaticnoise.multiplatformswiftpackage.domain.getConfigurationOrThrow
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
                project,
                packageTemplate = configuration.packageTemplate,
                toolsVersion = configuration.swiftToolsVersion,
                packageName = configuration.packageName,
                distributionMode = configuration.distributionMode,
                outputDirectory = configuration.outputDirectory,
                targetPlatforms = configuration.targetPlatforms,
                appleTargets = configuration.appleTargets
            )

            SimpleTemplateEngine()
                .run {
                    when (val file = packageConfiguration.templateFile) {
                        is TemplateFile.Resource -> createTemplate(file.url)
                        is TemplateFile.File -> createTemplate(file.value)
                    }
                }
                .make(packageConfiguration.templateProperties)
                .writeTo(packageFile.writer())
        }
    }
}
