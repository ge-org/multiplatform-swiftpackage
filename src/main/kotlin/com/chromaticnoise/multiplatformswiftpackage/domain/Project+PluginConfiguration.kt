package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.MultiplatformSwiftPackagePlugin
import com.chromaticnoise.multiplatformswiftpackage.SwiftPackageExtension
import com.chromaticnoise.multiplatformswiftpackage.domain.PluginConfiguration.PluginConfigurationError
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

@Throws(InvalidUserDataException::class)
internal fun Project.getConfigurationOrThrow() = PluginConfiguration.of(
    extensions.findByName(MultiplatformSwiftPackagePlugin.EXTENSION_NAME) as SwiftPackageExtension
).fold({
    throw InvalidUserDataException(
        "${it.toErrorMessage()}\nFind more information on https://github.com/ge-org/multiplatform-swiftpackage"
    )
}, { it })

private fun List<PluginConfigurationError>.toErrorMessage() = joinToString("\n\n") {
    when (it) {
        PluginConfigurationError.MissingSwiftToolsVersion -> """
        * Swift tools version is missing.
          Declare it by adding your Swift version to the plugin configuration block.
        """.trimIndent()
        PluginConfigurationError.MissingTargetPlatforms -> """
        * Target platforms are missing.
          Declare at least one platform by adding it to the plugin configuration block.
        """.trimIndent()
        PluginConfigurationError.MissingAppleTargets -> """
        * No Apple targets declared.
          It appears your multiplatform project does not contain any Apple target. 
        """.trimIndent()
    }
}
