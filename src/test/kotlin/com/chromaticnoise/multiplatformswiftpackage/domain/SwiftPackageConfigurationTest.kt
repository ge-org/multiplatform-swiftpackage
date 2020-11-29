package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate.TemplateFile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.mockk.mockk
import java.io.File

class SwiftPackageConfigurationTest : StringSpec() {

    init {
        "tools version property should match the tools version name" {
            configuration()
                .copy(toolsVersion = SwiftToolVersion.of("42")!!)
                .templateProperties["toolsVersion"].shouldBe("42")
        }

        "name property should match the configured package name" {
            configuration()
                .copy(packageName = PackageName.of("expected name").orNull!!)
                .templateProperties["name"].shouldBe("expected name")
        }

        "is-local property should be true if distribution mode is local" {
            configuration()
                .copy(distributionMode = DistributionMode.Local)
                .templateProperties["isLocal"].shouldBe(true)
        }

        "is-local property should be false if distribution mode is remote" {
            configuration()
                .copy(distributionMode = DistributionMode.Remote(DistributionURL("")))
                .templateProperties["isLocal"].shouldBe(false)
        }

        "url property should be null if distribution mode is local" {
            configuration()
                .copy(distributionMode = DistributionMode.Local)
                .templateProperties["url"].shouldBeNull()
        }

        "url property should match the value of the remote distribution url" {
            (configuration()
                .copy(distributionMode = DistributionMode.Remote(DistributionURL("my url")))
                .templateProperties["url"] as String).shouldStartWith("my url")
        }

        "url property should end with the zip file name" {
            (configuration()
                .copy(distributionMode = DistributionMode.Remote(DistributionURL("my url")))
                .templateProperties["url"] as String).shouldEndWith(".zip")
        }
    }

    private fun configuration() = SwiftPackageConfiguration(
        project = mockk(relaxed = true),
        packageTemplate = SwiftPackageTemplate(TemplateFile.File(File("")), emptyMap()),
        toolsVersion = SwiftToolVersion.of("42")!!,
        packageName = PackageName.of("package name").orNull!!,
        distributionMode = DistributionMode.Local,
        outputDirectory = OutputDirectory(File("")),
        targetPlatforms = emptyList(),
        appleTargets = emptyList()
    )
}
