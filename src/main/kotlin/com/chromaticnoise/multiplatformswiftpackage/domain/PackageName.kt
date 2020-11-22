package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError.InvalidPackageName

internal class PackageName private constructor(val value: String) {

    internal companion object {
        fun of(name: String?): Either<PluginConfigurationError, PackageName> =
            name?.ifNotBlank { Either.Right(PackageName(it)) }
            ?: Either.Left(InvalidPackageName(name))
    }

    override fun equals(other: Any?): Boolean = value == (other as? PackageName)?.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = "PackageName(value='$value')"
}
