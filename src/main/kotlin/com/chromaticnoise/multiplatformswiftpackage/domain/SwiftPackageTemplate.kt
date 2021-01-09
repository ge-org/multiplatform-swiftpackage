package com.chromaticnoise.multiplatformswiftpackage.domain

import com.chromaticnoise.multiplatformswiftpackage.dsl.PackageTemplateDSL
import java.net.URL

internal data class SwiftPackageTemplate(
    val file: TemplateFile,
    val configure: PackageTemplateDSL.() -> Unit
) {

    internal sealed class TemplateFile {
        data class Resource(val url: URL) : TemplateFile()
        data class File(val value: java.io.File) : TemplateFile()
    }
}
