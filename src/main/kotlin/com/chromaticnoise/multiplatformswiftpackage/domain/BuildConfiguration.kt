package com.chromaticnoise.multiplatformswiftpackage.domain

import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

internal sealed class BuildConfiguration {
    internal object Release : BuildConfiguration()
    internal object Debug : BuildConfiguration()
    internal data class Custom(val configurationName: String) : BuildConfiguration()

    val name get() = when(this) {
        Release -> NativeBuildType.RELEASE.getName()
        Debug -> NativeBuildType.DEBUG.getName()
        is Custom -> configurationName
    }
}
