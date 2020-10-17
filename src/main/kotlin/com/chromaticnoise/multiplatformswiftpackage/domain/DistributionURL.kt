package com.chromaticnoise.multiplatformswiftpackage.domain

internal data class DistributionURL(val value: String) {
    private val slashTerminatedValue: String get() =
        value.takeIf { it.endsWith("/") } ?:
        "$value/"

    fun appendPath(path: String) = DistributionURL("$slashTerminatedValue$path")
}
