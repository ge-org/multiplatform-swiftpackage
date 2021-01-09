package com.chromaticnoise.multiplatformswiftpackage.dsl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PackageTemplateDSLTest : StringSpec() {

    init {
        "setting property by key should add it to the extras" {
            val dsl = dsl()

            dsl["key"] = "value"

            dsl.extraProperties["key"] shouldBe "value"
        }

        "setting property by template key should add it to the extras" {
            TemplateKey.values().forEach { key ->
                val dsl = dsl()

                dsl[key] = "value"

                dsl.extraProperties[key.value] shouldBe "value"
            }
        }
    }

    private fun dsl() = PackageTemplateDSL(
        toolsVersion = "version",
        packageName = "package name",
        platforms = mutableListOf(),
        remoteDistribution = RemoteDistribution("url", "name", "checksum"),
        extraProperties = mutableMapOf()
    )
}
