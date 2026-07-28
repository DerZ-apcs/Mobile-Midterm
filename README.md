# The Code Cup

The Code Cup is a native Android coffee shop ordering app built for the CS426 Mobile Device Application Development midterm project. Users can browse coffee products, customize drinks, manage a persistent cart, place orders, earn loyalty rewards, redeem points, review orders, and edit their profile.

## Tech Stack

- Native Android
- Java source code
- XML layouts
- MVVM with ViewModel and LiveData
- Room for structured local persistence
- SharedPreferences for lightweight profile and settings data
- RecyclerView for dynamic lists
- Material Components for standard UI elements

## Features

- Branded splash screen and home catalog
- Coffee search and favorite toggles
- Coffee detail screen with quantity, shot, temperature, size, ice, and note customization
- Dynamic price calculation for customized drinks
- Persistent cart with edit, swipe-delete, aggregate totals, and cart badges
- Checkout flow with delivery address, promo handling, scheduling, and order success state
- Order history with reorder and review support
- Loyalty stamp tracking up to 8 stamps
- Reward points history and reward redemption
- Editable user profile persisted locally
- Light and dark theme support

## Project Structure

```text
app/src/main/java/com/example/midterm_application/
├── data/
│   ├── local/          Room database and DAO classes
│   ├── model/          App data models and Room entities
│   └── repository/     Data access and business coordination
├── ui/                 RecyclerView adapters
├── utils/              Price, reward, and checkout calculation helpers
├── viewmodel/          ViewModels for screen state and business logic
├── MainActivity.java   Main app navigation and primary screens
└── DetailActivity.java Coffee customization screen
```

## Requirements

- Android Studio with Android SDK 36 installed
- JDK 17 or newer
- Android device or emulator running API 24 or newer

## Build And Run

Open the project in Android Studio and run the `app` configuration, or use Gradle from the project root:

```bash
./gradlew assembleDebug
```

The generated debug APK is created under:

```text
app/build/outputs/apk/debug/
```

## Verification Commands

Run these commands from the project root before submission:

```bash
./gradlew clean
./gradlew test
./gradlew lintDebug
./gradlew assembleDebug
```

## Notes

- The application package is `com.example.midterm_application`.
- Structured app data is stored locally with Room and survives app restarts.
- Profile and theme settings are stored with SharedPreferences.
- The project intentionally uses Java and XML only, with no Kotlin source files and no Jetpack Compose.
