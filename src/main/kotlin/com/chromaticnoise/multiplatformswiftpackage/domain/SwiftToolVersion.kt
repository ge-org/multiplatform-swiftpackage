package com.chromaticnoise.multiplatformswiftpackage.domain

internal sealed class SwiftToolVersion {
    internal object Unknown : SwiftToolVersion()
    internal data class Named(val name: String) : SwiftToolVersion()
}
