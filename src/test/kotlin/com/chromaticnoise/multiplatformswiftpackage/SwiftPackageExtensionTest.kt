package com.chromaticnoise.multiplatformswiftpackage

import com.chromaticnoise.multiplatformswiftpackage.domain.BuildConfiguration
import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionMode
import com.chromaticnoise.multiplatformswiftpackage.domain.DistributionURL
import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate
import com.chromaticnoise.multiplatformswiftpackage.dsl.PackageTemplateDSL
import groovy.lang.Closure
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.kotlin.dsl.closureOf
import org.gradle.util.ClosureBackedAction
import java.io.File

class SwiftPackageExtensionTest : DescribeSpec() {

    init {
        describe("package name") {
            val ext = SwiftPackageExtension(project)

            it("has no default") {
                ext.packageName shouldBe null
            }

            it("can be set") {
                ext.packageName("new name")
                ext.packageName!!.orNull!!.value shouldBeEqualComparingTo "new name"
            }
        }

        describe("output directory") {
            val ext = SwiftPackageExtension(project)

            it("defaults to the project dir") {
                ext.outputDirectory.value shouldBeEqualComparingTo File(project.projectDir, "swiftpackage")
            }

            it("can be set") {
                ext.outputDirectory(File("new file"))
                ext.outputDirectory.value shouldBeEqualComparingTo File("new file")
            }
        }

        describe("swift tools version") {
            val ext = SwiftPackageExtension(project)

            it("has no default") {
                ext.swiftToolsVersion shouldBe null
            }

            it("can be set") {
                ext.swiftToolsVersion("new version")
                ext.swiftToolsVersion!!.name shouldBeEqualComparingTo "new version"
            }
        }

        describe("build configuration") {
            val ext = SwiftPackageExtension(project)

            it("should default to release") {
                ext.buildConfiguration shouldBe BuildConfiguration.Release
            }

            it("invokes the DSL configuration") {
                var invoked = false
                ext.buildConfiguration(action { invoked = true })
                invoked shouldBe true
            }

            it("can be set") {
                ext.buildConfiguration(action { debug() })
                ext.buildConfiguration shouldBe BuildConfiguration.Debug
            }
        }

        describe("distribution mode") {
            val ext = SwiftPackageExtension(project)

            it("should default to local") {
                ext.distributionMode shouldBe DistributionMode.Local
            }

            it("invokes the DSL configuration") {
                var invoked = false
                ext.distributionMode(action { invoked = true })
                invoked shouldBe true
            }

            it("can be set") {
                ext.distributionMode(action { remote("some url") })
                ext.distributionMode shouldBe DistributionMode.Remote(DistributionURL("some url"))
            }
        }

        describe("target platforms") {
            val ext = SwiftPackageExtension(project)

            it("should default to an empty collection") {
                ext.targetPlatforms.shouldBeEmpty()
            }

            it("invokes the DSL configuration") {
                var invoked = false
                ext.targetPlatforms(action { invoked = true })
                invoked shouldBe true
            }

            it("can be set") {
                ext.targetPlatforms(action { iOS(action { v("42") }) })
                ext.targetPlatforms.shouldNotBeEmpty()
            }
        }

        describe("package template") {
            val ext = SwiftPackageExtension(project)

            it("should default to the bundled template file") {
                ext.packageTemplate.file.shouldBeInstanceOf<SwiftPackageTemplate.TemplateFile.Resource>()
            }

            it("can be configured") {
                var invoked = false
                ext.packageTemplate(File("")) { invoked = true }
                ext.packageTemplate.configure(PackageTemplateDSL("", "", mutableListOf(), null, mutableMapOf()))
                invoked shouldBe true
            }

            it("can be configured by Groovy") {
                var invoked = false
                ext.packageTemplate(File(""), closure { invoked = true })
                ext.packageTemplate.configure(PackageTemplateDSL("", "", mutableListOf(), null, mutableMapOf()))
                invoked shouldBe true
            }

            it("can configure the default template file") {
                var invoked = false
                ext.packageTemplate { invoked = true }
                ext.packageTemplate.configure(PackageTemplateDSL("", "", mutableListOf(), null, mutableMapOf()))
                invoked shouldBe true
            }

            it("can configure the default template file from Groovu") {
                var invoked = false
                ext.packageTemplate(closure { invoked = true })
                ext.packageTemplate.configure(PackageTemplateDSL("", "", mutableListOf(), null, mutableMapOf()))
                invoked shouldBe true
            }

            it("can set the template file") {
                ext.packageTemplate(File("the file"))
                (ext.packageTemplate.file as SwiftPackageTemplate.TemplateFile.File).value shouldBeEqualComparingTo File("the file")
            }
        }
    }

    private fun <T> action(f: T.() -> Unit) = ClosureBackedAction<T>(closureOf(f))

    private fun <T> closure(f: T.() -> Unit) = closureOf<T>(f) as Closure<T>

    private val project: Project get() = mockk(relaxed = true) {
        every { projectDir } returns File("project dir")
    }
}
