package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.slashTerminatedUrl

/**
 * DSL to configure the rendering of the Package.swift file.
 *
 * @param toolsVersion Version of the Swift tools. That's the version added to the Package.swift header.
 * @param packageName Name of the Swift package
 * @param platforms All target platforms
 * @param remoteDistribution Null if the framework is to be distributed locally.
 *                           Otherwise represents info about the remote distribution.
 */
public data class PackageTemplateDSL internal constructor(
    public var toolsVersion: String,
    public var packageName: String,
    public var platforms: MutableCollection<Platform>,
    public var remoteDistribution: RemoteDistribution?,
    internal val extraProperties: MutableMap<String, Any?> = mutableMapOf()
) {

    /**
     * Sets the [value] as a custom property with the given [key].
     */
    public operator fun set(key: String, value: Any?) { extraProperties[key] = value }
    public operator fun set(templateKey: TemplateKey, value: Any?) { set(templateKey.value, value) }

    public operator fun get(key: String): Any? = extraProperties[key]
    public operator fun get(key: TemplateKey): Any? = extraProperties[key.value]
}

/**
 * Information related to distributing the framework remotely.
 *
 * @param baseUrl Base URL of the [fullUrl]. The initial value is guaranteed to end with a slash (/).
 * @param zipFileName Name of the ZIP file.
 * @param zipFileChecksum Checksum of the ZIP file.
 */
public class RemoteDistribution internal constructor(
    baseUrl: String,
    public var zipFileName: String,
    public var zipFileChecksum: String
) {

    public var baseUrl: String = slashTerminatedUrl(baseUrl)

    /**
     * URL pointing to the ZIP file.
     * Calculated by concatenating the [baseUrl] and [zipFileName] with a slash (/).
     */
    public val fullUrl: String get() = "${slashTerminatedUrl(baseUrl)}$zipFileName"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RemoteDistribution) return false

        if (zipFileName != other.zipFileName) return false
        if (zipFileChecksum != other.zipFileChecksum) return false
        if (baseUrl != other.baseUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = zipFileName.hashCode()
        result = 31 * result + zipFileChecksum.hashCode()
        result = 31 * result + baseUrl.hashCode()
        return result
    }

    override fun toString(): String =
        "RemoteDistribution(zipFileName='$zipFileName', zipFileChecksum='$zipFileChecksum', baseUrl='$baseUrl')"
}

/**
 * Information about a target platform.
 *
 * @param name The name of the platform. E.g. iOS
 * @param version The version targeted by the platform.
 */
public data class Platform(
    public var name: String,
    public var version: String
)

/**
 * Key used in the default Package.swift template to access the associated property.
 */
public enum class TemplateKey(internal val value: String) {
    /**
     * Version of the Swift tools.
     *
     * @see [PackageTemplateDSL.toolsVersion]
     */
    ToolsVersion("toolsVersion"),

    /**
     * Name of the package.
     *
     * @see [PackageTemplateDSL.packageName]
     */
    Name("name"),

    /**
     * Target platforms.
     *
     * @see [PackageTemplateDSL.platforms]
     */
    Platforms("platforms"),

    /**
     * Indicates if the framework will be distributed locally or remotely.
     *
     * @see [PackageTemplateDSL.remoteDistribution]
     */
    IsLocal("isLocal"),

    /**
     * URL of the ZIP file.
     *
     * @see [RemoteDistribution.fullUrl]
     */
    Url("url"),

    /**
     * Checksum of the ZIP file.
     *
     * @see [RemoteDistribution.zipFileChecksum]
     */
    Checksum("checksum")
}
