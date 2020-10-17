package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.TargetName
import com.chromaticnoise.multiplatformswiftpackage.domain.TargetPlatform
import com.chromaticnoise.multiplatformswiftpackage.dsl.TargetPlatformDsl.PlatformVersionDsl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll
import org.gradle.api.Action

class TargetPlatformDslTest : StringSpec() {

    init {
        "adding ios targets should add arm 64 target" {
            TargetPlatformDsl().apply { iOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("iosArm64")
        }

        "adding ios targets should add x64 target" {
            TargetPlatformDsl().apply { iOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("iosX64")
        }

        "adding watchOS targets should add arm 32 target" {
            TargetPlatformDsl().apply { watchOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("watchosArm32")
        }

        "adding watchOS targets should add arm 64 target" {
            TargetPlatformDsl().apply { watchOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("watchosArm64")
        }

        "adding watchOS targets should add x86 target" {
            TargetPlatformDsl().apply { watchOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("watchosX86")
        }

        "adding tvOS targets should add arm 64 target" {
            TargetPlatformDsl().apply { tvOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("tvosArm64")
        }

        "adding tvOS targets should add x64 target" {
            TargetPlatformDsl().apply { tvOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("tvosX64")
        }

        "adding macOS targets should add x64 target" {
            TargetPlatformDsl().apply { macOS(someVersion()) }.targetPlatforms
                .shouldHaveTarget("macosX64")
        }

        "adding target without names should not add a platform target" {
            TargetPlatformDsl().apply { targets(version = someVersion()) }.targetPlatforms
                .shouldBeEmpty()
        }

        "adding target with empty name should not add a platform target" {
            TargetPlatformDsl().apply { targets("", version = someVersion()) }.targetPlatforms
                .shouldBeEmpty()
        }

        "adding target with blank name should not add a platform target" {
            forAll(Arb.string().filter { it.isBlank() }) {
                TargetPlatformDsl().apply { targets("", version = someVersion()) }.targetPlatforms
                    .isEmpty()
            }
        }

        "adding target with invalid version should not add a platform target" {
            TargetPlatformDsl().apply { targets("target", version = invalidVersion()) }.targetPlatforms
                .shouldBeEmpty()
        }
    }

    private fun someVersion() = object : Action<PlatformVersionDsl> {
        override fun execute(t: PlatformVersionDsl) {
            t.v("13")
        }
    }

    private fun invalidVersion() = object : Action<PlatformVersionDsl> {
        override fun execute(t: PlatformVersionDsl) {
            t.v("")
        }
    }

    private fun Collection<TargetPlatform>.shouldHaveTarget(name: String) = firstOrNull {
        it.targets.contains(TargetName.of(name)!!)
    }.shouldNotBeNull()
}
