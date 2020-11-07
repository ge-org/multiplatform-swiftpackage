package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.*
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.InvalidTargetName
import org.gradle.api.Action

/**
* DSL to create instances of [TargetPlatform].
*/
public class TargetPlatformDsl {
    internal var targetPlatforms = mutableListOf<Either<List<PluginConfigurationError>, TargetPlatform>>()

    /**
     * Adds all iOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun iOS(version: Action<PlatformVersionDsl>) {
        targets(Either.Right(TargetName.IOSarm64), Either.Right(TargetName.IOSx64), version = version)
    }

    /**
     * Adds all watchOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun watchOS(version: Action<PlatformVersionDsl>) {
        targets(
            Either.Right(TargetName.WatchOSarm32),
            Either.Right(TargetName.WatchOSarm64),
            Either.Right(TargetName.WatchOSx86),
            Either.Right(TargetName.WatchOSx64),
            version = version
        )
    }

    /**
     * Adds all tvOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun tvOS(version: Action<PlatformVersionDsl>) {
        targets(Either.Right(TargetName.TvOSarm64), Either.Right(TargetName.TvOSx64), version = version)
    }

    /**
     * Adds all macOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun macOS(version: Action<PlatformVersionDsl>) {
        targets(Either.Right(TargetName.MacOSx64), version = version)
    }

    /**
     * Adds a [TargetPlatform] with targets for the given [names] and the provided [version].
     * The [names] corresponds to the Kotlin multiplatform target names.
     *
     * @param names of the targets. E.g. iosArm64, iosX64
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun targets(vararg names: String, version: Action<PlatformVersionDsl>) {
        val targetNames = names.map { Either.ofNullable(TargetName.of(it), InvalidTargetName(it)) }.toTypedArray()
        targets(*targetNames, version = version)
    }

    private fun targets(vararg names: Either<PluginConfigurationError, TargetName>, version: Action<PlatformVersionDsl>) {
        if (names.isEmpty()) return

        version.build(PlatformVersionDsl()) { dsl ->
            dsl.version?.let { platformVersion ->
                val errors = names.filterIsInstance<Either.Left<PluginConfigurationError, TargetName>>().map { it.value }
                val targetNames = names.filterIsInstance<Either.Right<PluginConfigurationError, TargetName>>().map { it.value }
                val platform: Either<List<PluginConfigurationError>, TargetPlatform> = when {
                    errors.isNotEmpty() -> Either.Left(errors)
                    else -> Either.Right(TargetPlatform(version = platformVersion, targets = targetNames))
                }
                targetPlatforms.add(platform)
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
