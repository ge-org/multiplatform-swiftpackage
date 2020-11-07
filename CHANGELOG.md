# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
tbd

## [1.0.2]
### Fixed
- Fix a bug where target names were not always resolved correctly
[#1](https://github.com/ge-org/multiplatform-swiftpackage/issues/1)

- Fix a bug where the creation of the XCFramework failed if dSYM files did not exist

## [1.0.1]
### Added
- Add all architectures of a platform at once
    ```kotlin
    targetPlatforms {
      iOS { v("13") }
      macOS { v("10.0") }
    }
    ```
- Better error messages if the plugin is not configured correctly
- Better validation of plugin configuration

## [1.0.0] - 2020-10-12
The first release :partying_face:
