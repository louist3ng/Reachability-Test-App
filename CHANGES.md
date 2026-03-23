# Changes Made

## What Changed

The project was converted from a blank Kotlin/Compose template app into a Java-based Android application designed for security reachability analysis testing.

## Why

The app serves as an intentionally vulnerable target so that security tools can practice detecting different types of vulnerabilities and tracing how they can be reached through the code.

## What Was Added

### App Screens
- **MainActivity** - Home screen with buttons to navigate to each demo screen. Also holds hardcoded credentials used across the app.
- **NetworkActivity** - Demonstrates insecure network calls: plain HTTP requests and HTTPS with certificate validation turned off. Includes a hidden analytics call that only triggers during error handling.
- **StorageActivity** - Demonstrates insecure data storage: writing secrets to world-readable preferences and external storage, plus logging sensitive data.
- **SqlActivity** - Demonstrates SQL injection: user input is directly inserted into a database query without sanitization. A hidden "drop table" command is triggered by long-pressing the login button.
- **ExposedActivity** - A "hidden admin panel" that displays credentials and can be opened by any app on the device without permission.
- **PhantomService** - An empty background service that any app can start, with no permission protection.

### Supporting Files
- **UserDatabaseHelper** - Sets up a local database with a test user for the SQL demo.
- **network_security_config.xml** - Allows all network traffic including unencrypted HTTP.
- **AndroidManifest.xml** - Declares all components, permissions, and exported activities/services.
- **README.md** - Documents every vulnerability, what it tests, and how to trigger it via ADB.

## What Was Removed
- All Kotlin source files and Compose UI code
- Kotlin and Compose Gradle plugins and dependencies
- Gradle version catalog (libs.versions.toml)
- Adaptive icon resources (incompatible with the lower min SDK)
- Old test stubs

## Build Configuration Changes
- Language changed from Kotlin to Java
- Package changed from `com.example.pocrechabilityanalysis` to `com.test.reachability`
- Min SDK lowered from 28 to 21 for broader device coverage
- Target SDK changed from 36 to 33
- Code minification and resource shrinking disabled
- All build types set to debuggable for easier analysis
