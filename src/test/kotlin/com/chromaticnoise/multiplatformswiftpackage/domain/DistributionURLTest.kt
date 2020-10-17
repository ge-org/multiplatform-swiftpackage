package com.chromaticnoise.multiplatformswiftpackage.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DistributionURLTest : StringSpec({

    "when appending a path it should be separated by a / from the URL" {
        val url = DistributionURL("my-url").appendPath("my-path")

        url.value.shouldBe("my-url/my-path")
    }
})
