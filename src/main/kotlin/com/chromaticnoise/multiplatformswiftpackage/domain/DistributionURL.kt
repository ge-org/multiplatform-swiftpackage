package com.chromaticnoise.multiplatformswiftpackage.domain

internal data class DistributionURL(val value: String) {
    fun appendPath(path: String) = DistributionURL("${slashTerminatedUrl(value)}$path")
}
