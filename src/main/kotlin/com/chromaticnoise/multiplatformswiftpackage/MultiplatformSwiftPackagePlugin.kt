package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.AppleTarget
import com.chromaticnoise.multiplatformswiftpackage.task.registerCreateSwiftPackageTask
import com.chromaticnoise.multiplatformswiftpackage.task.registerCreateXCFrameworkTask
import com.chromaticnoise.multiplatformswiftpackage.task.registerCreateZipFileTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Plugin to generate XCFramework and Package.swift file for Apple platform targets.
 */
internal class MultiplatformSwiftPackagePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, SwiftPackageExtension::class.java, project)
        project.afterEvaluate {
            project.extensions.findByType<KotlinMultiplatformExtension>()?.let { kmpExtension ->
                extension.appleTargets = AppleTarget.allOf(
                    nativeTargets = kmpExtension.targets.toList(),
                    platforms = extension.targetPlatforms
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
