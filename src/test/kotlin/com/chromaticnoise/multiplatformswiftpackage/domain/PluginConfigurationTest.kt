package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.SwiftPackageExtension
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.*
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import java.io.File

class PluginConfigurationTest : BehaviorSpec() {

    init {
        Given("an extension") {
            val extension = SwiftPackageExtension(project)

            When("the swift tool version is null") {
                extension.swiftToolsVersion = null

                Then("an error should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.contains(MissingSwiftToolsVersion)
                }
            }

            When("the target platforms produced errors") {
                val expectedErrors = listOf(InvalidTargetName("invalid name"), InvalidTargetName("whatever"))
                extension.targetPlatforms = listOf(Either.Left(expectedErrors))

                Then("the errors should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.containsAll(expectedErrors)
                }
            }

            When("the target platforms are empty") {
                extension.targetPlatforms = emptyList()

                Then("an error should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.contains(MissingTargetPlatforms)
                }
            }

            When("the apple platforms are empty but target platforms are not") {
                extension.targetPlatforms = listOf(Either.Right(TargetPlatform(PlatformVersion.of("13")!!, listOf(TargetName.IOSarm64))))
                extension.appleTargets = emptyList()

                Then("an error should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.contains(MissingAppleTargets)
                }
            }

            When("the package name produced errors") {
                val expectedError = BlankPackageName
                extension.packageName = Either.Left(expectedError)

                Then("an error should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.contains(expectedError)
                }
            }

            When("the package name is null and no apple framework exists") {
                extension.packageName = null
                extension.appleTargets = emptyList()

                Then("an error should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.contains(BlankPackageName)
                }
            }

            When("the package name is null and apple frameworks exist") {
                val expectedName = "expected name"
                val framework = AppleFramework(AppleFrameworkOutputFile(mockk()), AppleFrameworkName(expectedName), AppleFrameworkLinkTask(""))
                extension.swiftToolsVersion = SwiftToolVersion.of("42")
                extension.packageName = null
                extension.appleTargets = listOf(
                    mockk { every { getFramework(any()) } returns framework }
                )

                Then("the base name of the first framework should be used") {
                    PluginConfiguration.of(extension).orNull!!.packageName.value shouldBe expectedName
                }
            }

            When("the ZIP file name produced errors") {
                val expectedError = BlankZipFileName
                extension.zipFileName = Either.Left(expectedError)

                Then("an error should be returned") {
                    (PluginConfiguration.of(extension) as Either.Left).value.contains(expectedError)
                }
            }
        }
    }

    private lateinit var project: Project

    override fun beforeTest(testCase: TestCase) {
        project = mockk(relaxed = true) {
            every { projectDir } returns File("")
        }
    }
}
