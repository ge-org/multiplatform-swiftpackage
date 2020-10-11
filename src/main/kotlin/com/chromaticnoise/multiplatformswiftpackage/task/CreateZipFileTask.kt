package com.chromaticnoise.multiplatformswiftpackage.task

import com.chromaticnoise.multiplatformswiftpackage.domain.extension
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

internal fun Project.registerCreateZipFileTask() {
    tasks.register("createZipFile", Zip::class.java) {
        setGroup(null) // hide the task from the task list
        description = "Creates a ZIP file containing the XCFramework"

        dependsOn("createXCFramework")

        val outputDirectory = project.extension.outputDirectory.value
        archiveFileName.set(zipFileName(project))
        destinationDirectory.set(outputDirectory)
        from(outputDirectory) {
            include("**/*.xcframework/")
        }
    }
}
