# [<img src="./assets/alliance.svg" width="40" align="left" alt="Allidroid Manager">](https://github.com/FlibatsUniverse/AllidroidManager) Allidroid Manager

[![Version](https://img.shields.io/github/v/release/FlibatsUniverse/AllidroidManager?include_prereleases&style=for-the-badge&logo=github&logoColor=caa7f5&label=&color=caa7f5&labelColor=101018)](https://github.com/FlibatsUniverse/AllidroidManager/releases/latest)
[![Build](https://img.shields.io/github/actions/workflow/status/FlibatsUniverse/AllidroidManager/build.yml?branch=main&style=for-the-badge&logo=githubactions&logoColor=caa7f5&label=Build&labelColor=101018)](https://github.com/FlibatsUniverse/AllidroidManager/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/License-OSL--3.0-caa7f5?style=for-the-badge&logo=opensourceinitiative&logoColor=caa7f5&labelColor=101018)](LICENSE)
[![Stars](https://img.shields.io/github/stars/FlibatsUniverse/AllidroidManager?style=for-the-badge&logo=github&logoColor=caa7f5&color=caa7f5&labelColor=101018)](https://github.com/FlibatsUniverse/AllidroidManager/stargazers)
[![Discord](https://img.shields.io/badge/Discord-Join-caa7f5?style=for-the-badge&logo=discord&logoColor=caa7f5&labelColor=101018)](https://discord.com/invite/XszKXdqaZP)

Install and update [Allidroid](https://github.com/FlibatsUniverse/Allidroid), the mobile version of [Allicord](https://github.com/FlibatsUniverse/Allicord), by Alliance.

## Features

- Installs and updates Allidroid on Android without root, straight from the app
- Downloads the latest Discord, patches it, and installs the result for you
- Update notifications for Discord and for the manager itself
- Saved install logs you can open, copy, share, or delete
- Warns you before a large download on mobile data and when Play Protect might interfere
- Material 3 Expressive design with dynamic color, the Alliance palette, and light, dark, and pitch black themes
- Optional Shizuku installer, plus a downgrade option for devices that allow it

## Instructions

#### Prerequisites

- [Git](https://git-scm.com/downloads)
- [JDK 17](https://learn.microsoft.com/java/openjdk/download#openjdk-17)
- [Android SDK](https://developer.android.com/studio) with platform 36

#### Steps

1. Clone the repo
    - `git clone https://github.com/FlibatsUniverse/AllidroidManager.git && cd AllidroidManager`
2. Point Gradle at your Android SDK by creating `local.properties` with `sdk.dir=<path to your SDK>`
3. Build the project
    - Linux: `chmod +x ./gradlew && ./gradlew assembleDebug`
    - Windows: `./gradlew assembleDebug`
4. Install on a device or emulator
    - `adb install app/build/outputs/apk/debug/app-debug.apk`

## Credits

Allidroid Manager is made by Alliance Development, led by [Kuraa](https://github.com/ItzFlibat). Vendetta Lead: [Fiery](https://github.com/FieryFlames), [Maisy](https://github.com/maisymoe) and [Wing](https://github.com/wingio). Special thanks to [rushii](https://github.com/rushiiMachine) and [Xinto](https://github.com/X1nto).

## Star History

<a href="https://star-history.com/#FlibatsUniverse/AllidroidManager&Timeline">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=FlibatsUniverse/AllidroidManager&type=Timeline&theme=dark" />
    <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=FlibatsUniverse/AllidroidManager&type=Timeline" />
    <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=FlibatsUniverse/AllidroidManager&type=Timeline" />
  </picture>
</a>
