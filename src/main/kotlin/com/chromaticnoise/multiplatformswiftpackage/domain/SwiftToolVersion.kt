package com.chromaticnoise.multiplatformswiftpackage.domain

internal class SwiftToolVersion private constructor(private val value: String) {

    val name get() = value

    companion object {
        fun of(name: String) = name.ifNotBlank { SwiftToolVersion(it) }
    }

    override fun equals(other: Any?): Boolean = value == (other as? SwiftToolVersion)?.value

    override fun hashCode(): Int = value.hashCode()
}
