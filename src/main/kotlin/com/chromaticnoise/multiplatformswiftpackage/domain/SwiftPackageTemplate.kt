package com.chromaticnoise.multiplatformswiftpackage.domain

import java.net.URL

internal data class SwiftPackageTemplate(
    val file: TemplateFile,
    val properties: Map<String, Any?>
) {

    internal sealed class TemplateFile {
        data class Resource(val url: URL) : TemplateFile()
        data class File(val value: java.io.File) : TemplateFile()
    }
}
