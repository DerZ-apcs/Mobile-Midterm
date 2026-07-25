# Architecture Plan: Project setup and Java/XML migration

## Existing Files

- `agent/CURRENT_TASK.md` defines this feature as the initial migration from Kotlin/Compose to a compiling Java/XML foundation.
- `AGENTS.md` requires native Android, Java only, XML layouts only, MVVM-compatible package structure, no Compose, no Kotlin source files, and a build after every feature.
- `PROJECT_SPEC.md` confirms this is feature 1 in the implementation order and requires package `com.example.midterm_application` with future subpackages `data/model`, `data/local`, `data/repository`, `ui`, `viewmodel`, and `utils`.
- `Midterm Project.md` requires an Android app with multi-screen behavior later, but this task is only setup and migration.
- `app/src/main/java/com/example/midterm_application/MainActivity.kt` is a starter Compose activity using `ComponentActivity`, `setContent`, `Scaffold`, Compose `Text`, preview code, and `MidtermApplicationTheme`.
- `app/src/main/java/com/example/midterm_application/ui/theme/Color.kt`, `Type.kt`, and `Theme.kt` are Compose/Kotlin theme scaffold files.
- `app/src/test/java/com/example/midterm_application/ExampleUnitTest.kt` and `app/src/androidTest/java/com/example/midterm_application/ExampleInstrumentedTest.kt` are Kotlin test files and violate the Java-only rule if left in the app source tree.
- `app/build.gradle.kts` applies `libs.plugins.kotlin.compose`, enables `buildFeatures.compose = true`, and depends on Compose, KTX, and Compose test artifacts.
- Root `build.gradle.kts` declares the Kotlin Compose plugin alias.
- `gradle/libs.versions.toml` currently contains Compose, Kotlin, KTX, and test library aliases.
- `app/src/main/AndroidManifest.xml` already points to `.MainActivity`, package namespace/application id is `com.example.midterm_application`, and app theme is `@style/Theme.MidtermApplication`.
- `app/src/main/res/values/themes.xml`, `strings.xml`, and `colors.xml` exist and can support a simple XML-based activity.
- Launcher icons, backup rules, data extraction rules, and keep rules already exist and are unrelated to this setup task.

## Required Changes

- Remove Kotlin/Compose app source files from `app/src/main/java/com/example/midterm_application/`.
- Remove Kotlin test source files or replace them with Java equivalents only if keeping smoke tests is useful and minimal.
- Add `app/src/main/java/com/example/midterm_application/MainActivity.java` using a standard Android Java Activity class.
- Add the future package directories required by `AGENTS.md` and `PROJECT_SPEC.md`: `data/model`, `data/local`, `data/repository`, `ui`, `viewmodel`, and `utils`. Because empty directories are not tracked by git, use minimal Java package marker classes only if the implementation needs committed package placeholders; otherwise create directories during implementation and let later feature files populate them.
- Add a base XML layout resource such as `app/src/main/res/layout/activity_main.xml` with a simple launch screen for The Code Cup.
- Keep `AndroidManifest.xml` pointing at `.MainActivity`; update only if the Java activity package/path changes.
- Update `app/build.gradle.kts` to remove Compose build features, Compose dependencies, Kotlin/Compose plugin usage, KTX dependencies, and Compose debug/androidTest artifacts.
- Keep only minimal Java/XML-compatible dependencies required for this setup. AppCompat or AndroidX Activity can be used if needed, but the most minimal option is a plain Android framework `Activity` or `AppCompatActivity` only if the dependency is explicitly added.
- Update root `build.gradle.kts` and `gradle/libs.versions.toml` to remove unused Kotlin/Compose plugin and dependency aliases that are no longer referenced.
- Preserve `namespace` and `applicationId` as `com.example.midterm_application`.

## Data Flow

- No feature data flow should be implemented in this task.
- `MainActivity.java` should only inflate the XML layout with `setContentView(R.layout.activity_main)`.
- Do not add ViewModels, repositories, DAOs, Room database classes, seed data, product models, cart logic, rewards logic, profile persistence, or navigation state yet.
- The Java/XML foundation should leave a clean package structure for later UI -> ViewModel -> Repository -> DAO -> Room flows.

## UI And Navigation Flow

- The app should launch to `MainActivity` through the existing launcher intent in `AndroidManifest.xml`.
- `MainActivity` should display a simple XML layout that identifies the app as The Code Cup or otherwise confirms the Java/XML foundation is active.
- Do not implement Home, Details, Cart, Rewards, Redeem, Profile, order history, bottom navigation, fragments, RecyclerViews, or placeholder screens that imply those features are complete.
- Any setup UI should be explicitly minimal and temporary foundation UI, not a partial feature implementation.
- Back navigation can remain default single-activity behavior for this task.

## Persistence And Threading

- No persistent data should be introduced in this task.
- Do not add Room entities, DAOs, repositories, seed callbacks, executors, or SharedPreferences usage yet.
- No background database threading is needed because no database work should exist.
- The setup should avoid any main-thread long-running work.

## Edge Cases

- All Kotlin source files under `app/src/main`, `app/src/test`, and `app/src/androidTest` must be removed or converted to Java so the project satisfies the no-Kotlin-source completion criterion.
- Compose must be fully removed from Gradle configuration: no Compose plugin, no `buildFeatures.compose`, no Compose BOM, no Compose runtime/UI/material/tooling/test dependencies.
- Removing Kotlin/Compose dependencies may expose missing Java-compatible dependencies if `MainActivity.java` extends an AndroidX class. Use either a framework activity with no new dependency or add the smallest necessary AndroidX dependency.
- The XML layout must be in `res/layout`; otherwise `R.layout.activity_main` will not compile.
- The manifest activity declaration can remain `.MainActivity` only if the Java class is in package `com.example.midterm_application`.
- Resource names and theme names referenced by the manifest must remain valid.
- If Kotlin Gradle DSL files remain (`*.gradle.kts`), that is acceptable because the task prohibits Kotlin source files in the app source tree and Kotlin/Compose app code, not Gradle Kotlin DSL.
- If Java package directories are created as placeholders, avoid adding business logic or misleading feature classes.

## Regression Risks

- Removing Compose dependencies while leaving Compose imports or theme files will break compilation.
- Removing the Kotlin plugin without deleting Kotlin tests will break test source compilation or leave source files that violate project rules.
- Changing package names or moving `MainActivity` outside `com.example.midterm_application` would break the manifest or completion criteria.
- Over-editing resources could break launcher icon, backup, or theme references unrelated to the setup feature.
- Adding future feature placeholders could create false completion signals and conflict with later feature agents.

## Build Risks

- `compileSdk` is configured with AGP 9.3 syntax using `compileSdk { version = release(36) { minorApiLevel = 1 } }`; if the local SDK lacks API 36.1, `./gradlew assembleDebug` may fail independently of the migration logic.
- Removing version catalog aliases requires ensuring no Gradle files still reference those aliases.
- If AndroidX AppCompat is chosen, the version catalog must include an AppCompat alias and the activity theme may need an AppCompat-compatible parent. To minimize risk, prefer a plain `android.app.Activity` unless later setup requirements need AppCompat.
- Java source compatibility is already set to Java 11 and should remain valid.
- Instrumented test dependencies may be left only if Java instrumented tests remain; otherwise unused test dependencies can be removed to keep setup minimal.

## Step-By-Step Plan

1. Delete the Compose/Kotlin main source files: `MainActivity.kt` and `ui/theme/*.kt`.
2. Delete or convert the Kotlin unit and instrumented test files so no `*.kt` files remain under `app/src`.
3. Add `MainActivity.java` in `app/src/main/java/com/example/midterm_application/` using package `com.example.midterm_application`, extending a minimal Java-compatible activity, and calling `setContentView(R.layout.activity_main)` in `onCreate`.
4. Add `app/src/main/res/layout/activity_main.xml` with a simple XML layout for the setup launch screen.
5. Ensure `AndroidManifest.xml` still references `.MainActivity`, `@string/app_name`, and `@style/Theme.MidtermApplication` correctly.
6. Clean `app/build.gradle.kts` by removing the Kotlin Compose plugin, `buildFeatures.compose`, Compose dependencies, KTX dependencies, and Compose debug/androidTest artifacts.
7. Clean root `build.gradle.kts` and `gradle/libs.versions.toml` so unused Kotlin/Compose aliases are removed and only minimal needed Android/test dependencies remain.
8. Add the required Java package structure for future features without implementing feature behavior.
9. Run a full search for `*.kt`, `compose`, `kotlin`, and `setContent` under the app source and Gradle files to confirm Compose/Kotlin app code is gone.
10. Run `./gradlew assembleDebug` and fix only migration-related build, resource, manifest, or import issues.
11. The implementation report should document changed files, build result, assumptions, and incomplete items, but that report belongs to the implementation/build agents, not this architecture step.
