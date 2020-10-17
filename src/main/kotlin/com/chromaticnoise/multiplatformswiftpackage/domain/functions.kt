package com.chromaticnoise.multiplatformswiftpackage.domain

fun <T> String.ifNotBlank(f: (String) -> T?): T? = takeIf { it.isNotBlank() }?.let { f(it) }
