package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionURL

/**
 * DSL to create instance of a [DistributionModeDSL].
 */
public class DistributionModeDSL {
    internal var distributionMode: DistributionMode = DistributionMode.Local

    /**
     * The XCFramework will be distributed via the local file system.
     */
    public fun local() {
        distributionMode = DistributionMode.Local
    }

    /**
     * The XCFramework will be distributed via a ZIP file that can be downloaded from the [url].
     *
     * @param url where the ZIP file can be downloaded from. E.g. https://example.com/packages/
     */
    public fun remote(url: String) {
        distributionMode = DistributionMode.Remote(DistributionURL(url))
    }
}
