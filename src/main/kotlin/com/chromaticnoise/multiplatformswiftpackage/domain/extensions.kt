package com.chromaticnoise.multiplatformswiftpackage.domain

import org.gradle.api.Action
import org.jetbrains.kotlin.konan.target.Family

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
