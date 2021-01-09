package com.chromaticnoise.multiplatformswiftpackage.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.comparables.shouldBeEqualComparingTo

class RemoteDistributionTest : StringSpec({
    "base URL initially ends with a slash" {
        RemoteDistribution("base url", "zip file name", "")
            .baseUrl shouldBeEqualComparingTo "base url/"
    }

    "base URL is not automatically slash-terminated after changing it" {
        val data = RemoteDistribution("base url", "zip file name", "")
        data.baseUrl = "no slash at the end"
        data.baseUrl shouldBeEqualComparingTo "no slash at the end"
    }

    "full URL contains base URL and ZIP file name" {
        RemoteDistribution("base url", "zip file name", "")
            .fullUrl shouldBeEqualComparingTo "base url/zip file name"
    }
})
