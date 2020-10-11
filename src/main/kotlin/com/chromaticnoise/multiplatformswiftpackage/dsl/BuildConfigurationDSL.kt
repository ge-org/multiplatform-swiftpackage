package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.BuildConfiguration

/**
 * DSL to create instance of a [BuildConfiguration].
 */
public class BuildConfigurationDSL {
    internal var buildConfiguration: BuildConfiguration = BuildConfiguration.Release

    /**
     * XCode release configuration.
     */
    public fun release() { buildConfiguration = BuildConfiguration.Release }

    /**
     * XCode debug configuration.
     */
    public fun debug() { buildConfiguration = BuildConfiguration.Debug }

    /**
     * Custom configuration.
     *
     * @param name of the custom configuration.
     */
    public fun named(name: String) { buildConfiguration = BuildConfiguration.Custom(name) }
}
