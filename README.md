# Multiplatform Swift Package

[![Build Status](https://travis-ci.com/ge-org/multiplatform-swiftpackage.svg?branch=master)](https://travis-ci.com/ge-org/multiplatform-swiftpackage)

This is a Gradle plugin for Kotlin Multiplatform projects that generates an XCFramework for your native Apple targets
and creates a matching Package.swift file to distribute it as a binary target.

To distribute the framework a ZIP file containing it will be created and referenced from the Package.
You can upload the ZIP file to your package registry so that SPM can download it.

## Prerequisites
* XCode version 12.0+
* Gradle version 6.0+

## Installing
The plugin is published on Maven central. Add it to the `plugins` block in the Gradle build file.

```kotlin
plugins {
  id("com.chromaticnoise.multiplatform-swiftpackage") version "1.0.2"
}
```

## Execution
The plugin adds two Gradle tasks to your project.

* ```./gradlew createSwiftPackage```

    Creates the XCFramework, ZIP file and Package.swift


* ```./gradlew createXCFramework```

    Creates only the XCFramework

## Configuration
Generation of the Package and XCFramework can be configured using the plugin's DSL.
Add the configuration to the Gradle build file.

This is a complete example of the required configuration. All available options will be explained in the following sections.

```kotlin
multiplatformSwiftPackage {
    swiftToolsVersion("5.3")
    targetPlatforms {
      iOS { v("13") }
    }
}
```

### Output Directory
By default, the plugin will write all files into the _swiftpackage_ folder in the project directory.
You can configure the output folder by providing a File object pointing to it.

```kotlin
outputDirectory(File(projectDir, "swiftpackage"))
```

### Swift Tools Version
The first line of every Package.swift file is a header declaring the Swift tools version  required by the package.
To set the version use the following configuration and provide the version your XCode project supports.

```kotlin
swiftToolsVersion("5.3")
```

### Distribution Mode
Swift packages can distribute binary targets either via the local file system or via a remote ZIP file.
Depending on your requirements (e.g. local development or CI) use one of the following configurations.

#### Local distribution
```kotlin
distributionMode { local() }
```

#### Remote distribution
Provide a URL where the ZIP file containing the XCFramework is located.
This should point to the root directory and not to the ZIP file itself. 
```kotlin
// correct
distributionMode { remote("https://example.com") }

// wrong
distributionMode { remote("https://example.com/MyLib.zip") }
```

### Build Configuration
Apple frameworks can be built with different configurations. By default, these are _release_ and _debug_.
However, you can also create your own configurations.

#### Default
```kotlin
buildConfiguration { release() }
// or
buildConfiguration { debug() }
```

#### Custom
```kotlin
buildConfiguration { named("staging") }
```

### Target Platforms
The main feature of an XCFramework is packaging multiple architectures for the same platform.
This is great since it allows distributing e.g. iOS builds for both the physical device, and the simulator in one package.

Swift packages require declaring the minimum supported version for each platform.
Therefore, you need to configure both the architectures for each platform and the version.

You can either declare all target architectures specifically or add all architectures of a platform at once.

```kotlin
targetPlatforms {
  // all iOS targets (== device and simulator) with minimum version 13
  iOS { v("13") }

  // macOS with minimum version 10.0
  targets("macosX64") { v("10.0") }
}
```

Available platform shortcuts are:
- `iOS { v("xxx") }`
- `tvOS { v("xxx") }`
- `macOS { v("xxx") }`
- `watchOS { v("xxx") }`

## Further Reading
To learn more about the Swift Package Manager I recommend reading the following resources.

* https://swift.org/package-manager/
* https://github.com/apple/swift-package-manager
* https://docs.swift.org/package-manager/PackageDescription/PackageDescription.html

## License
```text
Copyright 2020 Georg Dresler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
