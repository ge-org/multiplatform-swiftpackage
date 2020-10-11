package com.chromaticnoise.multiplatformswiftpackage.domain

import java.net.URL

internal sealed class DistributionMode {
    internal object Local : DistributionMode()
    internal data class Remote(val url: URL) : DistributionMode();
}
