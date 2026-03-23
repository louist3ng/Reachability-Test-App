# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean build
```

## Architecture

Single-module Android app using:
- **Jetpack Compose** for declarative UI (no XML layouts)
- **Single Activity** pattern — `MainActivity` is the sole entry point
- **Material Design 3** with dynamic color support (Android 12+)

### Package Structure

`com.example.pocrechabilityanalysis`
- `MainActivity.kt` — App entry point, sets up Compose content
- `ui/theme/` — Theme, Color, and Typography definitions (Material 3)

### Build System

- Kotlin DSL (`.kts`) for all Gradle files
- Centralized dependency versions in `gradle/libs.versions.toml` (Version Catalog) — add new dependencies there, not directly in `build.gradle.kts`
- Kotlin 2.0.21, AGP 8.13.2, Compose BOM 2024.09.00
- `compileSdk 36`, `minSdk 28`, `targetSdk 36`, JVM target 11