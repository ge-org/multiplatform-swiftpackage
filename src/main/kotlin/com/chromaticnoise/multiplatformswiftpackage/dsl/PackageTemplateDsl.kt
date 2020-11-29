package com.chromaticnoise.multiplatformswiftpackage.dsl

import com.chromaticnoise.multiplatformswiftpackage.domain.SwiftPackageTemplate
import java.io.File

/**
 * DSL to create an instance of a [SwiftPackageTemplate].
 */
public class PackageTemplateDsl(file: File) {
    internal var packageTemplate: SwiftPackageTemplate = SwiftPackageTemplate(
        file = SwiftPackageTemplate.TemplateFile.File(file),
        properties = emptyMap()
    )

    /**
     * The properties that will be available in the template file when it is rendered.
     * Properties defined here will overwrite default properties with the same key.
     */
    public fun properties(properties: Map<String, Any?>) {
        packageTemplate = packageTemplate.copy(properties = properties)
    }
}
