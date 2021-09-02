package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.Either
import com.chromaticnoise.multiplatformswiftpackage.domain.PlatformVersion
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.InvalidTargetName
import com.chromaticnoise.multiplatformswiftpackage.domain.TargetName
import com.chromaticnoise.multiplatformswiftpackage.domain.TargetPlatform
import groovy.lang.Closure
import org.gradle.util.ConfigureUtil

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
    public fun iOS(version: PlatformVersionDsl.() -> Unit) {
        targetsInternal(
            listOf(
                Either.Right(TargetName.IOSarm64),
                Either.Right(TargetName.IOSx64),
                Either.Right(TargetName.IOSSimulatorArm64)
            ),
            version
        )
    }

    public fun iOS(version: Closure<PlatformVersionDsl>) {
        iOS { ConfigureUtil.configure(version, this) }
    }

    /**
     * Adds all watchOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun watchOS(version: PlatformVersionDsl.() -> Unit) {
        targetsInternal(
            listOf(
                Either.Right(TargetName.WatchOSarm32),
                Either.Right(TargetName.WatchOSarm64),
                Either.Right(TargetName.WatchOSx86),
                Either.Right(TargetName.WatchOSx64),
                Either.Right(TargetName.WatchOSSimulatorArm64)
            ),
            version
        )
    }

    public fun watchOS(version: Closure<PlatformVersionDsl>) {
        watchOS { ConfigureUtil.configure(version, this) }
    }

    /**
     * Adds all tvOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun tvOS(version: PlatformVersionDsl.() -> Unit) {
        targetsInternal(
            listOf(
                Either.Right(TargetName.TvOSarm64),
                Either.Right(TargetName.TvOSx64),
                Either.Right(TargetName.TvOSSimulatorArm64)
            ),
            version
        )
    }

    public fun tvOS(version: Closure<PlatformVersionDsl>) {
        tvOS { ConfigureUtil.configure(version, this) }
    }

    /**
     * Adds all macOS targets as a [TargetPlatform] using the provided [version].
     *
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun macOS(version: PlatformVersionDsl.() -> Unit) {
        targetsInternal(
            listOf(
                Either.Right(TargetName.MacOSx64),
                Either.Right(TargetName.MacOSArm64)
            ),
            version
        )
    }

    public fun macOS(version: Closure<PlatformVersionDsl>) {
        macOS { ConfigureUtil.configure(version, this) }
    }

    /**
     * Adds a [TargetPlatform] with targets for the given [names] and the provided [version].
     * The [names] corresponds to the Kotlin multiplatform target names.
     *
     * @param names of the targets. E.g. iosArm64, iosX64
     * @param version builder for an instance of [PlatformVersion]
     */
    public fun targets(vararg names: String, version: PlatformVersionDsl.() -> Unit) {
        targetsInternal(names.asList().toTargetNames(), version)
    }

    public fun targets(names: Collection<String>, version: Closure<PlatformVersionDsl>) {
        targetsInternal(names.toTargetNames()) { ConfigureUtil.configure(version, this) }
    }

    private fun targetsInternal(names: Collection<Either<PluginConfigurationError, TargetName>>, configure: PlatformVersionDsl.() -> Unit) {
        if (names.isEmpty()) return
        val platformVersion = PlatformVersionDsl().apply(configure).version ?: return

        val errors = names.filterIsInstance<Either.Left<PluginConfigurationError, TargetName>>().map { it.value }
        val targetNames = names.filterIsInstance<Either.Right<PluginConfigurationError, TargetName>>().map { it.value }
        val platform: Either<List<PluginConfigurationError>, TargetPlatform> = when {
            errors.isNotEmpty() -> Either.Left(errors)
            else -> Either.Right(TargetPlatform(version = platformVersion, targets = targetNames))
        }
        targetPlatforms.add(platform)
    }

    private fun Collection<String>.toTargetNames() = map { Either.ofNullable(TargetName.of(it), InvalidTargetName(it)) }

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
