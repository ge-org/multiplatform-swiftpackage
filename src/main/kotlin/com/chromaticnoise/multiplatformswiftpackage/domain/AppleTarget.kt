package com.chromaticnoise.multiplatformswiftpackage.domain

import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

internal class AppleTarget private constructor(val nativeTarget: KotlinNativeTarget) {

    internal fun framework(buildConfiguration: BuildConfiguration): Framework? =
        try {
            nativeTarget.binaries.getFramework(buildConfiguration.name)
        } catch (_: Exception) { null }

    internal companion object {
        fun allOf(
            nativeTargets: Collection<KotlinTarget>,
            platforms: Collection<TargetPlatform>
        ): Collection<AppleTarget> = nativeTargets
            .filterIsInstance<KotlinNativeTarget>()
            .filter { it.konanTarget.family.isAppleFamily }
            .filter { target ->
                platforms
                    .flatMap { platform -> platform.targets.map { it.name } }
                    .contains(target.name)
            }
            .map { AppleTarget(it) }
    }
}
