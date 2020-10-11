package com.chromaticnoise.multiplatformswiftpackage.domain

internal data class TargetPlatform(
    val version: PlatformVersion,
    val targets: Collection<TargetName>
)
