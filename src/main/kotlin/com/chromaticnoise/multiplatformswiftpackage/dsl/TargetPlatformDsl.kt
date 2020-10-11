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
     * Adds a [TargetPlatform] with targets for the given [names] and the provided [version].
     * The [names] corresponds to the Kotlin multiplatform target names.
     *
     * @param names of the targets. E.g. iosArm64, iosX64
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun targets(vararg names: String, version: Action<PlatformVersionDsl>) {
        version.build(PlatformVersionDsl()) { dsl ->
            targetPlatforms.add(TargetPlatform(
                version = dsl.version,
                targets = names.map { TargetName(it) }
            ))
        }
    }

    /**
     * DSL to create instances of [PlatformVersion].
     */
    public class PlatformVersionDsl {
        internal lateinit var version: PlatformVersion

        /**
         * Creates a [PlatformVersion] for the given [versionName].
         * The [versionName] corresponds to the SupportedPlatform section of a Package.swift file
         *
         * @param versionName of the platform version. E.g. 13, 11.0.1
         *
         * @see "https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html#supportedplatform"
         */
        public fun v(versionName: String) {
            this.version = PlatformVersion(versionName)
        }
    }
}
