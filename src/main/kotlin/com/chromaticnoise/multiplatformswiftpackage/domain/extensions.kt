package com.chromaticnoise.multiplatformswiftpackage.domain

import org.gradle.api.Action
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

internal fun <T> Action<T>.build(dsl: T, builder: (dsl: T) -> Unit) {
    execute(dsl)
    builder(dsl)
}

internal val Family.swiftPackagePlatformName get() = when (this) {
    Family.OSX -> "macOS"
    Family.IOS -> "iOS"
    Family.TVOS -> "tvOS"
    Family.WATCHOS -> "watchOS"
    else -> null
}

internal val Collection<Either<List<PluginConfiguration.PluginConfigurationError>, TargetPlatform>>.platforms get() =
    filterIsInstance<Either.Right<List<PluginConfiguration.PluginConfigurationError>, TargetPlatform>>().map { it.value }

internal val Collection<Either<List<PluginConfiguration.PluginConfigurationError>, TargetPlatform>>.errors get() =
    filterIsInstance<Either.Left<List<PluginConfiguration.PluginConfigurationError>, TargetPlatform>>().map { it.value }.flatten()

internal val TargetName.konanTarget: KonanTarget get() = when (this) {
    TargetName.IOSarm64 -> KonanTarget.IOS_ARM64
    TargetName.IOSx64 -> KonanTarget.IOS_X64
    TargetName.WatchOSarm32 -> KonanTarget.WATCHOS_ARM32
    TargetName.WatchOSarm64 -> KonanTarget.WATCHOS_ARM64
    TargetName.WatchOSx86 -> KonanTarget.WATCHOS_X86
    TargetName.WatchOSx64 -> KonanTarget.WATCHOS_X64
    TargetName.TvOSarm64 -> KonanTarget.TVOS_ARM64
    TargetName.TvOSx64 -> KonanTarget.TVOS_X64
    TargetName.MacOSx64 -> KonanTarget.MACOS_X64
}
