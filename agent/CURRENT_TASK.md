# Current Task

## Feature

Project setup and Java/XML migration

## Status

READY_FOR_ARCHITECTURE

## Read First

- `AGENTS.md`
- `PROJECT_SPEC.md`
- `Midterm Project.md`
- Current Android source tree

## Goal

Convert the starter Android project from Kotlin/Compose to a compiling Java/XML foundation that follows `AGENTS.md`.

## Allowed Changes

- Gradle dependency cleanup.
- Remove Kotlin/Compose starter files.
- Add Java package structure under `app/src/main/java/com/example/midterm_application/`.
- Add `MainActivity.java`.
- Add base XML layout resources.
- Update `AndroidManifest.xml` if needed.
- Add only dependencies required for Java/XML Android, MVVM, Room, RecyclerView, and bottom navigation.

## Not Allowed

- Do not implement Home, Details, Cart, Rewards, Redeem, Profile, or user-defined features yet.
- Do not add unnecessary dependencies.
- Do not skip build verification.
- Do not leave Kotlin or Compose app code behind.

## Required Output Reports

- Architecture: `outputs/01-setup-architecture.md`
- Implementation: `outputs/02-setup-implementation.md`
- Build/Test: `outputs/03-setup-build.md`
- Review: `outputs/04-setup-review.md`
- Fix, if needed: `outputs/05-setup-fix.md`

## Completion Criteria

- `./gradlew assembleDebug` passes.
- The app launches to a simple Java/XML `MainActivity` layout.
- The package remains `com.example.midterm_application`.
- No Kotlin source files remain in the app source tree.
- Compose is not enabled in Gradle.
