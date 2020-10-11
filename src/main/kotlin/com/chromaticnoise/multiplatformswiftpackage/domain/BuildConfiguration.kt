package com.chromaticnoise.multiplatformswiftpackage.domain

internal sealed class BuildConfiguration {
    internal object Release : BuildConfiguration()
    internal object Debug : BuildConfiguration()
    internal data class Custom(val configurationName: String) : BuildConfiguration()

    val name get() = when(this) {
        Release -> "RELEASE"
        Debug -> "DEBUG"
        is Custom -> configurationName
    }
}
