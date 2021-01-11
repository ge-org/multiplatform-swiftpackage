package com.chromaticnoise.multiplatformswiftpackage.domain

import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeOutputKind

internal class AppleTarget private constructor(val nativeTarget: KotlinNativeTarget) {

    internal fun getFramework(buildConfiguration: BuildConfiguration): AppleFramework? =
        try {
            val nativeBinary = nativeTarget.binaries.find { binary ->
                binary.buildType.getName().equals(buildConfiguration.name, ignoreCase = true) &&
                    binary.outputKind == NativeOutputKind.FRAMEWORK
            }
            AppleFramework.of(nativeBinary)
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
                    .flatMap { platform -> platform.targets.map { it.konanTarget } }
                    .firstOrNull { it == target.konanTarget } != null
            }
            .map { AppleTarget(it) }
    }
}
