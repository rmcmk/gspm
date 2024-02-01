# gspm - Gradle Submodule Package Manager

![License](https://img.shields.io/github/license/rmcmk/gspm)
[![Release](https://jitpack.io/v/rmcmk/gspm.svg)](https://jitpack.io/#rmcmk/gspm)

## Overview

**Note: This plugin is still a work in progress and not ready for production
use.**

The Gradle Submodule Package Manager (gspm) offers first-class support for Git
submodules, providing seamless integration as plugins, composite builds, and
dependencies within Gradle projects.

## Features

- [x] Recognizes submodules in a Git repository
- [x] Adds submodules as composite builds to a Gradle project
- [x] Adds submodules as plugins to a Gradle project
- [x] Support for submodules that are multi-project builds with decoupled
  dependency resolution
	- [x] Individual dependencies on subprojects
- [x] Adds submodules as dependencies to a Gradle project
- [x] Type-safe submodule configuration
- [x] Type-safe submodule dependency resolution via version catalogs
- [ ] Support for submodules that are not Gradle projects
- [ ] Support for nested submodules
- [ ] NPM-style submodule installation, i.e., `gradle gspm-install <submodule>`
	- [ ] Configurable install location, overridden via command line OR Gradle
	  extension
	- [ ] Install via repository URL
	- [ ] Install from commit hash
	- [ ] Install from git tag
	- [ ] Install from git branch
	- [ ] Automatically adds configuration (updating .gitmodules, generating
	  types, notifying Gradle, etc.)
- [ ] NPM-style submodule uninstallation,
  i.e., `gradle gspm-uninstall <submodule>`
	- [ ] Uninstall via repository URL
	- [ ] Uninstall from path
	- [ ] Uninstall from name
	- [ ] Automatically removes configuration
		- [ ] Removes the submodule entry from .git/config
		- [ ] Removes the submodule directory from the superproject's
		  .git/modules
		- [ ] Removes the entry in .gitmodules and removes the submodule
		  directory located at path/to/submodule
- [ ] NPM-style submodule update, i.e., `gradle gspm-update <submodule>`
- [ ] Commits for automated git state changes (install, uninstall, etc)
	- [ ] Enable/disable automatic commits
	- [ ] Configurable commit message
	- [ ] Option for 'commit only if working directory clean'
	- [ ] Sign-off support

## Roadmap (Work in Progress)

- Support for submodules that are not Gradle projects
- Support for nested submodules
- Automated git state changes with configurable commits and sign-off support

## Installation

_WIP: Detailed instructions will be provided upon stable release._

## FAQ

**Why gspm?**

Managing private or non-Maven repository dependencies, especially with Git
submodules, can be challenging. gspm solves this by making submodules
first-class citizens in Gradle, offering a more streamlined and flexible
dependency management approach. While not intended to replace package managers
like Artifactory, gspm complements them for teams requiring a more adaptable
solution.

---

**Why hasn't gspm been published on the Gradle Plugin Hub?**

gspm is currently in development and not yet suitable for production use. Once
it reaches a stable release, it will be published on the Gradle Plugin Hub. For
now, you can access release candidate builds through JitPack. Make sure to
configure the JitPack repository for plugins in your Gradle settings file.

Example - (_settings.gradle.kts_):

```kts
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "gspm") {
                useModule("com.github.rmcmk.gspm:gspm.gradle.plugin:${requested.version}")
            }
        }
    }
}

plugins {
    id("gspm") version "1.0.0-RC7"
}
```

## Non-Goals

gspm does not aim to replicate features like `npm run outdated` or extensive
versioning capabilities. Other tools are better suited for maintaining ideal
dependency versions and checking for vulnerabilities.

## Acknowledgements

- [Augustinas R](https://github.com/klepto/) - Provided substantial research and
  development, support, and the initial concept of the plugin.
- [Foundry](https://github.com/foundry-rs/foundry) - Served as inspiration for
  the plugin. Their work on Forge for the Solidity ecosystem has been
  influential.

## Contributing

We welcome contributions and feedback! Feel free to submit issues or pull
requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file
for details.
