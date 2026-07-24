# The Code Cup Development Rules

## Platform

- Native Android
- Java only
- XML layouts
- Android target

## Architecture

- MVVM
- ViewModel + LiveData
- Room for structured persistent data
- SharedPreferences for lightweight settings/profile values
- RecyclerView for lists

## Package Structure

- data/model
- data/local
- data/repository
- ui
- viewmodel
- utils

## Rules

1. Use Java only. Do not introduce Kotlin source files.
2. Do not introduce Compose.
3. Use XML layouts.
4. Do not add unnecessary dependencies.
5. UI code belongs in Activity/Fragment.
6. Business logic belongs in ViewModel or repository.
7. Database operations must not run on the main thread.
8. Use RecyclerView for lists.
9. Preserve existing architecture.
10. Do not modify unrelated features.
11. Inspect relevant existing classes before coding.
12. Build the project after every feature.

## Definition of Done

A task is complete only if:

- Project compiles.
- Feature works on Android.
- Existing navigation still works.
- State survives configuration changes where appropriate.
- Persistent data survives app restart.
- No unrelated files were unnecessarily modified.
