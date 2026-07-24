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

## Current Scaffold Warning

- If the project still contains starter Kotlin or Compose files, the first implementation task must remove that scaffold and replace it with a compiling Java/XML foundation.
- Do not leave Compose enabled or unused Compose dependencies after the migration task.
- Do not introduce Kotlin files at any later point.

## Gradle And Dependencies

- Keep dependencies minimal and relevant to the native Android Java/XML app.
- Allowed dependency categories: AndroidX appcompat/core, lifecycle ViewModel/LiveData, RecyclerView, Room, Material components if needed for bottom navigation or standard widgets, and test dependencies.
- Do not add networking, image-loading, dependency-injection, or navigation libraries unless a task explicitly justifies them.
- If Room is added, include the compiler/annotation processor configuration required for Java.

## Data And Threading

- Room entities, DAO interfaces, and database classes belong in `data/local` or `data/model` as appropriate.
- Repositories are the only layer that should coordinate DAO operations for ViewModels.
- Activities and Fragments must not directly access DAOs.
- All insert, update, delete, and long-running read operations must run off the main thread.
- LiveData from Room should be observed with the correct lifecycle owner.

## UI And Navigation

- UI code belongs in Activity/Fragment classes and XML layout resources.
- Adapters may bind data and dispatch user actions through callbacks, but they must not contain business logic.
- Use RecyclerView for product, cart, reward, and order-history lists.
- Keep bottom navigation behavior working after every feature.
- Details navigation must preserve the selected coffee id safely across configuration changes.

## Persistence Rules

- Use Room for coffee products, cart items, orders, reward transactions, and other structured state.
- Use SharedPreferences only for lightweight profile/settings values.
- Seed required coffee product data on first launch without duplicating rows on every launch.
- Persistent cart, rewards, orders, and profile values must survive app restart after their feature is implemented.

## Agent Workflow

For each feature, use this sequence:

1. Architecture agent creates a plan and does not modify code.
2. Implementation agent implements only the current feature.
3. Build/Test agent runs verification and fixes only build/resource/manifest/import issues.
4. Reviewer agent reviews the feature and does not modify code.
5. Fix agent fixes only reviewer issues classified BLOCKER, HIGH, or MEDIUM.
6. Build/Test agent verifies again before moving to the next feature.

## Current Task Control

- `agent/CURRENT_TASK.md` defines the active feature.
- Agents must read `agent/CURRENT_TASK.md` before starting.
- If `CURRENT_TASK.md` conflicts with `PROJECT_SPEC.md` or `Midterm Project.md`, stop and ask for clarification unless the conflict is clearly a narrower feature scope.

## Reports

- Write reports under `outputs/`.
- Do not overwrite unrelated reports.
- Use names that include the feature and agent role, such as `outputs/04-home-architecture.md`.
- Every implementation report must include files changed, build result, assumptions, and incomplete items.
- Every review report must include severity, file/area, problem, impact, and recommended fix.

## Prohibited Changes

- Do not refactor unrelated code.
- Do not modify unrelated features.
- Do not add placeholder screens that claim a feature is complete.
- Do not hide errors by disabling checks, removing required code, or weakening requirements.
- Do not run destructive git commands.

## Definition of Done

A task is complete only if:

- Project compiles.
- Feature works on Android.
- Existing navigation still works.
- State survives configuration changes where appropriate.
- Persistent data survives app restart.
- No unrelated files were unnecessarily modified.
- Required report file is written.
