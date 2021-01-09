package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate.TemplateFile
import com.chromaticnoise.multiplatformswiftpackage.dsl.PackageTemplateDSL
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

        "configuration closure should be invoked" {
            var wasInvoked = false
            configuration { wasInvoked = true }
                .templateProperties

            wasInvoked.shouldBeTrue()
        }

        "configuration closure should receive tools version" {
            verifyDsl({
                copy(toolsVersion = SwiftToolVersion.of("expected version")!!)
            }) {
                toolsVersion shouldBeEqualComparingTo "expected version"
            }
        }

        "configuration closure should receive package name" {
            verifyDsl({
                copy(packageName = PackageName.of("expected package name").orNull!!)
            }) {
                packageName shouldBeEqualComparingTo "expected package name"
            }
        }

        "configuration closure should not receive remote configuration if locally distributed" {
            verifyDsl({
                copy(distributionMode = DistributionMode.Local)
            }) {
                remoteDistribution shouldBe null
            }
        }

        "configuration closure should receive remote configuration if remotely distributed" {
            verifyDsl({
                copy(distributionMode = DistributionMode.Remote(DistributionURL("url")))
            }) {
                remoteDistribution shouldNotBe null
            }
        }

        "configuration closure should receive slash terminated remote distribution url" {
            verifyDsl({
                copy(distributionMode = DistributionMode.Remote(DistributionURL("url")))
            }) {
                remoteDistribution!!.baseUrl shouldEndWith "/"
            }
        }
    }

    private fun configuration(configuration: PackageTemplateDSL.() -> Unit = {}) = SwiftPackageConfiguration(
        project = mockk(relaxed = true),
        packageTemplate = SwiftPackageTemplate(TemplateFile.File(File("")), configuration),
        toolsVersion = SwiftToolVersion.of("42")!!,
        packageName = PackageName.of("package name").orNull!!,
        distributionMode = DistributionMode.Local,
        outputDirectory = OutputDirectory(File("")),
        targetPlatforms = emptyList(),
        appleTargets = emptyList()
    )

    private fun verifyDsl(c: SwiftPackageConfiguration.() -> SwiftPackageConfiguration, f: PackageTemplateDSL.() -> Unit) {
        var invoked = false
        c(configuration()).copy(
            packageTemplate = SwiftPackageTemplate(TemplateFile.File(File(""))) {
                invoked = true
                f(this)
            }
        ).templateProperties
        invoked.shouldBeTrue()
    }
}
