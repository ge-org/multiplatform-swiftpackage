package com.chromaticnoise.multiplatformswiftpackage.domain

internal class PlatformVersion private constructor(private val value: String) {
    val name: String get() = value

    internal companion object {
        fun of(name: String) = name.ifNotBlank { PlatformVersion(it) }
    }

    override fun equals(other: Any?): Boolean = value == (other as? PlatformVersion)?.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString() = "PlatformVersion(name='$name')"
}
