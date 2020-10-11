package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.MultiplatformSwiftPackagePlugin
import com.chromaticnoise.multiplatformswiftpackage.SwiftPackageExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.target.Family

internal val Project.extension get(): ExtensionConfiguration =
    (extensions.findByName(MultiplatformSwiftPackagePlugin.EXTENSION_NAME) as SwiftPackageExtension).container

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
