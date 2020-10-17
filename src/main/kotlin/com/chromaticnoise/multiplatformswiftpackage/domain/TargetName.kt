package com.chromaticnoise.multiplatformswiftpackage.domain

internal class TargetName private constructor(private val value: String) {

    val name: String get() = value

    internal companion object {
        fun of(name: String): TargetName? = name.ifNotBlank { TargetName(name) }
    }

    override fun equals(other: Any?): Boolean = value == (other as? TargetName)?.value

    override fun hashCode(): Int = value.hashCode()
}
