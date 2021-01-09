package com.chromaticnoise.multiplatformswiftpackage.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class FunctionsTest : StringSpec({

    "closure should not be invoked on blank string" {
        forAll(Arb.string().filter { it.isBlank() }) { str ->
            var invoked = false
            str.ifNotBlank { invoked = true }
            !invoked
        }
    }

    "closure should not be invoked on non-blank string" {
        forAll(Arb.string().filter { it.isNotBlank() }) { str ->
            var invoked = false
            str.ifNotBlank { invoked = true }
            invoked
        }
    }

    "when getting slash-terminated URL it should end with a slash" {
        forAll(Arb.string()) { url ->
            slashTerminatedUrl(url).endsWith("/")
        }
    }
})
