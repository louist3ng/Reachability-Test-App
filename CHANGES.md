# Changes Made

## What Changed

The project was converted from a blank Kotlin/Compose template app into a Java-based Android application designed for security reachability analysis testing.

## Why

The app serves as an intentionally vulnerable target so that security tools can practice detecting different types of vulnerabilities and tracing how they can be reached through the code.

## What Was Added

### App Screens
- **MainActivity** - Home screen with buttons to navigate to each demo screen. Also holds hardcoded credentials used across the app.
- **NetworkActivity** - Demonstrates insecure network calls: plain HTTP requests and HTTPS with certificate validation turned off.
- **StorageActivity** - Demonstrates insecure data storage: writing secrets to world-readable preferences and external storage, plus logging sensitive data.
- **SqlActivity** - Demonstrates SQL injection: user input is directly inserted into a database query without sanitization.
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

## MobSF android_rules.yaml Weakness Mapping

All vulnerability labels (both live and dead code) have been remapped to specific MobSF
`android_rules.yaml` rules. Each vulnerability's source code was verified (and where
necessary, rewritten) to contain the exact regex patterns that MobSF's scanner uses for
detection.

### Rule Mapping Summary

| Vulnerability | MobSF Rule ID | Pattern Verified |
|---|---|---|
| Hardcoded Credentials | `android_hardcoded` | `password\s*=\s*['"].{1,100}['"]`, `key\s*=\s*['"].{1,100}['"]` |
| Credential Logging | `android_logging` | `Log\.(d)` |
| Cleartext HTTP + IP | `android_ip_disclosure` | IP address regex (10.0.2.2) |
| Disabled SSL | `android_insecure_ssl` | `javax\.net\.ssl` AND `.setDefaultHostnameVerifier(` |
| Hidden Analytics | `android_ip_disclosure` | IP address regex (172.16.0.5) |
| World-Readable Prefs | `android_world_readable` | `MODE_WORLD_READABLE` |
| External Storage | `android_read_write_external` | `.getExternalStorage` |
| Sensitive Log Data | `android_logging` | `Log\.(d)` |
| SQL Injection | `android_sql_raw_query` | `android\.database\.sqlite` AND `rawQuery(` |
| Destructive SQL | `android_sql_raw_query` | `android\.database\.sqlite` AND `execSQL(` |
| Hidden UI Data | `android_hiddenui` | `setVisibility(View.GONE)` |
| Service Logging | `android_logging` | `Log\.(d)` |

### Code Changes Made
- **NetworkActivity.java**: Changed HTTP URL to include IP address (10.0.2.2); replaced `setHostnameVerifier()` with `setDefaultHostnameVerifier()`; restructured `sendToAnalytics()` to use `if(true){throw}` pattern so dead code compiles; added IP addresses to dead code URLs
- **StorageActivity.java**: Changed `getSharedPreferences("secrets", 1)` to use `MODE_WORLD_READABLE` constant
- **ExposedActivity.java**: Added hidden `View.GONE` element with backup secret to trigger `android_hiddenui`
- **PhantomService.java**: Added `Log.d()` calls to log intent data, triggering `android_logging`
- **DeadAdminClient.java**: Added `username` and `password` as hardcoded string fields; changed URL to IP address
- **LegacyDataUploader.java**: Added hardcoded `secret` field; changed URL to IP address
- All vulnerability labels updated from generic OWASP/MASVS references to specific MobSF rule IDs with regex patterns

## Weakness Separation

Dead code and unreachable vulnerability patterns have been separated from reachable vulnerability
files into dedicated orphaned classes for cleaner MobSF validation.

### What Changed

**Vulnerability files now contain ONLY reachable vulnerabilities:**
- **MainActivity.java** — Removed `leakCredentialsToUrl()`, `nukeDatabase()`, `writeCredsToExternalStorage()`
- **NetworkActivity.java** — Removed `if(false)` branch, `sendToAnalytics()` (dead code after throw), and calls to it
- **StorageActivity.java** — Removed `conditionallyDeleteSecrets()` (dead branch where shouldDelete=false)
- **SqlActivity.java** — Removed `if(debugMode==1)` dead branch, `executeAdminQuery()` (dead code after return), and long-click listener

**New dedicated dead code files (orphaned classes, never instantiated):**
- **DeadMainActivity.java** — Dead methods from MainActivity: credential leak via HTTP, database nuke, external storage write
- **DeadNetworkActivity.java** — Dead patterns from NetworkActivity: if(false) HTTP branch, analytics endpoint dead code
- **DeadStorageActivity.java** — Dead patterns from StorageActivity: conditional delete with always-false flag
- **DeadSqlActivity.java** — Dead patterns from SqlActivity: debug dump branch, admin query after return

**Existing dead code files (unchanged):**
- **DeadAdminClient.java** — Already an orphaned class
- **LegacyDataUploader.java** — Already an orphaned class

### Why
Separating dead code into dedicated files makes it easier to validate MobSF findings by comparing
which vulnerabilities are detected in "reachable" files vs "dead code" files. A correct reachability
analysis tool should flag vulnerabilities in the activity/service files but NOT in the Dead* files.

### Build Configuration
- AGP upgraded from 8.1.4 to 8.7.3 (fixes JDK 21 jlink compatibility)
- compileSdk/targetSdk upgraded from 33 to 35
