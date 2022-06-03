package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.InvalidTargetName
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl.PlatformVersionDsl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll

class TargetPlatformDslTest : StringSpec() {

    init {
        "adding ios targets should add arm 64 target" {
            TargetPlatformDsl().apply { iOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("iosArm64")
        }

        "adding ios targets should add x64 target" {
            TargetPlatformDsl().apply { iOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("iosX64")
        }

        "adding ios targets should add arm 64 simulator target" {
            TargetPlatformDsl().apply { iOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("iosSimulatorArm64")
        }

        "adding watchOS targets should add arm 32 target" {
            TargetPlatformDsl().apply { watchOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("watchosArm32")
        }

        "adding watchOS targets should add arm 64 target" {
            TargetPlatformDsl().apply { watchOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("watchosArm64")
        }

        "adding watchOS targets should add x86 target" {
            TargetPlatformDsl().apply { watchOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("watchosX86")
        }

        "adding watchOS targets should add arm 64 simulator target" {
            TargetPlatformDsl().apply { watchOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("watchosSimulatorArm64")
        }

        "adding tvOS targets should add arm 64 target" {
            TargetPlatformDsl().apply { tvOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("tvosArm64")
        }

        "adding tvOS targets should add x64 target" {
            TargetPlatformDsl().apply { tvOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("tvosX64")
        }

        "adding tvOS targets should add arm 64 simulator target" {
            TargetPlatformDsl().apply { tvOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("tvosSimulatorArm64")
        }

        "adding macOS targets should add x64 target" {
            TargetPlatformDsl().apply { macOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("macosX64")
        }

        "adding macOS targets should add arm 64 target" {
            TargetPlatformDsl().apply { macOS(someVersion) }.targetPlatforms
                .shouldHaveTarget("macosArm64")
        }

        "adding target without names should not add a platform target" {
            TargetPlatformDsl().apply { targets(version = someVersion) }.targetPlatforms
                .shouldBeEmpty()
        }

        "adding target with empty name should add an invalid-name error" {
            TargetPlatformDsl().apply { targets("", version = someVersion) }.targetPlatforms
                .shouldHaveError(InvalidTargetName(""))
        }

        "adding target with blank name should add an invalid-name error" {
            forAll(Arb.string().filter { it.isBlank() }) { name ->
                TargetPlatformDsl().apply { targets(name, version = someVersion) }.targetPlatforms.errors.firstOrNull {
                    it == InvalidTargetName(name)
                } != null
            }
        }

        "adding target with unknown name should add an invalid-name error" {
            forAll(Arb.string().filter { TargetName.of(it) == null }) { name ->
                TargetPlatformDsl().apply { targets(name, version = someVersion) }.targetPlatforms.errors.firstOrNull {
                    it == InvalidTargetName(name)
                } != null
            }
        }

        "adding target with invalid version should not add a platform target" {
            TargetPlatformDsl().apply { targets("target", version = invalidVersion) }.targetPlatforms
                .shouldBeEmpty()
        }
    }

    private val someVersion: (PlatformVersionDsl) -> Unit = { it.v("13") }
    private val invalidVersion: (PlatformVersionDsl) -> Unit = { it.v("") }

    private fun Collection<Either<List<PluginConfigurationError>, TargetPlatform>>.shouldHaveTarget(name: String) =
        platforms.firstOrNull {
            it.targets.contains(TargetName.of(name)!!)
        }.shouldNotBeNull()

    private fun Collection<Either<List<PluginConfigurationError>, TargetPlatform>>.shouldHaveError(expectedError: PluginConfigurationError) =
        errors.firstOrNull {
            it == expectedError
        }.shouldNotBeNull()
}
