package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.InvalidPackageName
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class PackageNameTest : StringSpec({

    "empty name should not be valid" {
        PackageName.of("").leftValueOrNull!!.shouldBeInstanceOf<InvalidPackageName>()
    }

    "blank names should not be valid" {
        forAll(Arb.string().filter { it.isBlank() }) { name ->
            PackageName.of(name).leftValueOrNull is InvalidPackageName
        }
    }

    "two instances should be equal if their names are equal" {
        (PackageName.of("equal name") == PackageName.of("equal name"))
            .shouldBeTrue()
    }

    "two instances should not be equal if their names differ" {
        (PackageName.of("some name") == PackageName.of("different name"))
            .shouldBeFalse()
    }
})
