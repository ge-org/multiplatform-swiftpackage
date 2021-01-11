package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.BlankZipFileName
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class ZipFileNameTest : StringSpec({

    "empty name should not be valid" {
        ZipFileName.of("").leftValueOrNull!!.shouldBeInstanceOf<BlankZipFileName>()
    }

    "blank names should not be valid" {
        forAll(Arb.string().filter { it.isBlank() }) { name ->
            ZipFileName.of(name).leftValueOrNull is BlankZipFileName
        }
    }

    "two instances should be equal if their names are equal" {
        (ZipFileName.of("equal name") == ZipFileName.of("equal name"))
            .shouldBeTrue()
    }

    "two instances should not be equal if their names differ" {
        (ZipFileName.of("some name") == ZipFileName.of("different name"))
            .shouldBeFalse()
    }

    "file extension should be .zip" {
        forAll(Arb.string().filter { it.isNotBlank() }) { name ->
            ZipFileName.of(name).orNull!!.nameWithExtension.endsWith(".zip")
        }
    }
})
