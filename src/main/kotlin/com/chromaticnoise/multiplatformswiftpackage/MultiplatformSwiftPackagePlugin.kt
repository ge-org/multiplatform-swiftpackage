package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.AppleTarget
import com.chromaticnoise.multiplatformswiftpackage.domain.platforms
import com.chromaticnoise.multiplatformswiftpackage.task.registerCreateSwiftPackageTask
import com.chromaticnoise.multiplatformswiftpackage.task.registerCreateXCFrameworkTask
import com.chromaticnoise.multiplatformswiftpackage.task.registerCreateZipFileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Plugin to generate XCFramework and Package.swift file for Apple platform targets.
 */
public class MultiplatformSwiftPackagePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<SwiftPackageExtension>(EXTENSION_NAME, project)

        project.afterEvaluate {
            project.extensions.findByType<KotlinMultiplatformExtension>()?.let { kmpExtension ->
                extension.appleTargets = AppleTarget.allOf(
                    nativeTargets = kmpExtension.targets.toList(),
                    platforms = extension.targetPlatforms.platforms
                )
                project.registerCreateXCFrameworkTask()
                project.registerCreateZipFileTask()
                project.registerCreateSwiftPackageTask()
            }
        }
    }

    internal companion object {
        internal const val EXTENSION_NAME = "multiplatformSwiftPackage"
    }
}
