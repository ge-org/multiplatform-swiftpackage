package com.chromaticnoise.multiplatformswiftpackage.domain

internal enum class TargetName(val identifier: String) {
    IOSarm64("iosArm64"),
    IOSx64("iosX64"),
    IOSSimulatorArm64("iosSimulatorArm64"),
    WatchOSarm32("watchosArm32"),
    WatchOSarm64("watchosArm64"),
    WatchOSx86("watchosX86"),
    WatchOSx64("watchosX64"),
    WatchOSSimulatorArm64("watchosSimulatorArm64"),
    TvOSarm64("tvosArm64"),
    TvOSx64("tvosX64"),
    TvOSSimulatorArm64("tvosSimulatorArm64"),
    MacOSx64("macosX64"),
    MacOSArm64("macosArm64");

    internal companion object {
        private val map = values().associateBy(TargetName::identifier)
        fun of(id: String): TargetName? = map[id]
    }
}
