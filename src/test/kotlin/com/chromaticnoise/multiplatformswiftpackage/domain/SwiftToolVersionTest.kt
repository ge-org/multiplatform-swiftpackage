package com.chromaticnoise.multiplatformswiftpackage.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class SwiftToolVersionTest : StringSpec({

    "empty name should not be valid" {
        SwiftToolVersion.of("").shouldBeNull()
    }

    "blank names should not be valid" {
        forAll(Arb.string().filter { it.isBlank() }) { name ->
            SwiftToolVersion.of(name) == null
        }
    }

    "two instances should be equal if their names are equal" {
        (SwiftToolVersion.of("equal name") == SwiftToolVersion.of("equal name"))
            .shouldBeTrue()
    }

    "two instances should not be equal if their names differ" {
        (SwiftToolVersion.of("some name") == SwiftToolVersion.of("different name"))
            .shouldBeFalse()
    }
})
