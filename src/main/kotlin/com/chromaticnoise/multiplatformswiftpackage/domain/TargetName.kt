package com.chromaticnoise.multiplatformswiftpackage.domain

internal enum class TargetName(val identifier: String) {
    IOSsimulatorArm64("iosSimulatorArm64"),
    IOSarm64("iosArm64"),
    IOSx64("iosX64"),
    WatchOSarm32("watchosArm32"),
    WatchOSarm64("watchosArm64"),
    WatchOSx86("watchosX86"),
    WatchOSx64("watchosX64"),
    TvOSarm64("tvosArm64"),
    TvOSx64("tvosX64"),
    MacOSx64("macosX64");

    internal companion object {
        private val map = values().associateBy(TargetName::identifier)
        fun of(id: String): TargetName? = map[id]
    }
}
