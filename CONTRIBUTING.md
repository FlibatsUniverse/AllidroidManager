# Contributing to Allidroid Manager

## Branches and pull requests

main is the stable Allidroid Manager branch. Create feature branches from main and target main. Never force-push main. Every main push is built by the Allidroid Manager Build workflow, and a release is published only when a maintainer runs the Allidroid Manager Release workflow.

## Conventional Commits

Use Conventional Commits for new commits and pull request titles:

```text
feat(home): show installed version on the installation card
fix(installer): retry a failed download once
docs(contributing): describe the release process
ci(release): pin action versions
```

Format: type(optional-scope): description. Types include feat, fix, docs, style, refactor, perf, test, build, ci, chore and revert. Use ! or a BREAKING CHANGE: footer for breaking changes. Squash titles must follow this format. History-preserving merge commits keep their original messages.

## Code

Follow .rules for Kotlin, Compose, strings and branding. Use existing Material 3 components and the existing theme. Put new screens in ui/screen/<name> with their model in ui/viewmodel/<name>, and installer steps in installer/step. User-facing text belongs in `app/src/main/res/values/strings.xml`, in US spelling. See [ARCHITECTURE.md](ARCHITECTURE.md) for where things live.

## Validation

Install JDK 17 and the Android SDK with platform 36, then run `./gradlew assembleDebug`. Install the APK on a device or emulator and test the screens you changed. Review workflow changes and dependency compatibility. Compilation alone does not validate behavior.

## Related repositories

Allidroid Manager installs [Allidroid](https://github.com/FlibatsUniverse/Allidroid), the mobile version of [Allicord](https://github.com/FlibatsUniverse/Allicord). Problems with the app itself belong in the Allidroid repository, and problems with the desktop and web client belong in the Allicord repository.

## AI assistance

Disclose the tool and scope of AI assistance. Human contributors must review and understand submitted changes. Keep issues and pull request descriptions concise and relevant.
