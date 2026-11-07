# Architecture

Allidroid Manager is a single-module Android app written in Kotlin with Jetpack Compose and Material 3 Expressive. It downloads Discord, patches it with Allidroid (released from the Allidroid repository), and installs the result. The home screen shows recent commits from that repository.

Code lives in `app/src/main/java/com/flibatsuniverse/allidroid/manager`:

- `di` wires dependencies with Koin.
- `domain` holds the preference, install and download managers, plus the repository and package receiver.
- `network` has the Ktor HTTP service, GitHub and tracker DTOs, and response helpers.
- `installer` is the install pipeline. `step` contains the ordered download, patch and install steps run by `StepRunner`. `session` and `shizuku` are the two package installers. `util` has the patching, signing and logging helpers.
- `ui` has screens, view models, widgets, shared components and the theme. Screens are navigated with Voyager.
- `updatechecker` checks for new Discord versions in the background and shows notifications.
- `utils` has small shared helpers, including the saved log store.

## Branding

Names, links and the modded app's package name are `BuildConfig` fields in `app/build.gradle.kts`. The color palette is in `ui/theme/BrandColors.kt`. Dynamic color from the system wallpaper replaces it on Android 12 and newer when enabled.

## Logs

Each install writes its log to the app's private `logs` folder when it ends. The Logs screen lists, opens, shares and deletes them.
