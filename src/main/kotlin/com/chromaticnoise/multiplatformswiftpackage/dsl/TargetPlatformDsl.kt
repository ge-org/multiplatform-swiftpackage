package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.PlatformVersion
import com.chromaticnoise.multiplatformswiftpackage.domain.TargetName
import com.chromaticnoise.multiplatformswiftpackage.domain.TargetPlatform
import com.chromaticnoise.multiplatformswiftpackage.domain.build
import org.gradle.api.Action

/**
* DSL to create instances of [TargetPlatform].
*/
public class TargetPlatformDsl {
    internal var targetPlatforms = mutableListOf<TargetPlatform>()

    /**
     * Adds all iOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun iOS(version: Action<PlatformVersionDsl>) {
        targets("iosArm64", "iosX64", version = version)
    }

    /**
     * Adds all watchOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun watchOS(version: Action<PlatformVersionDsl>) {
        targets("watchosArm32", "watchosArm64", "watchosX86", version = version)
    }

    /**
     * Adds all tvOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun tvOS(version: Action<PlatformVersionDsl>) {
        targets("tvosArm64", "tvosX64", version = version)
    }

    /**
     * Adds all macOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun macOS(version: Action<PlatformVersionDsl>) {
        targets("macosX64", version = version)
    }

    /**
     * Adds a [TargetPlatform] with targets for the given [names] and the provided [version].
     * The [names] corresponds to the Kotlin multiplatform target names.
     *
     * @param names of the targets. E.g. iosArm64, iosX64
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun targets(vararg names: String, version: Action<PlatformVersionDsl>) {
        if (names.isEmpty()) return
        if (names.any { it.isBlank() }) return

        version.build(PlatformVersionDsl()) { dsl ->
            dsl.version?.let { platformVersion ->
                targetPlatforms.add(TargetPlatform(
                    version = platformVersion,
                    targets = names.mapNotNull { TargetName.of(it) }
                ))
            }
        }
    }

    /**
     * DSL to create instances of [PlatformVersion].
     */
    public class PlatformVersionDsl {
        internal var version: PlatformVersion? = null

        /**
         * Creates a [PlatformVersion] for the given [versionName].
         * The [versionName] corresponds to the SupportedPlatform section of a Package.swift file
         *
         * @param versionName of the platform version. E.g. 13, 11.0.1
         *
         * @see "https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#supportedplatform"
         */
        public fun v(versionName: String) {
            PlatformVersion.of(versionName)?.let {
                this.version = it
            }
        }
    }
}
