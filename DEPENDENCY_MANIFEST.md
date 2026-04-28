# APK Dependency Manifest

> Auto-generated inventory of all dependencies in the ReachabilityTestApp APK.
> Includes direct, transitive, local, native, and dynamic (runtime-only) dependencies.

## Remote Dependencies (Gradle-resolved)

Resolved via `./gradlew app:dependencies --configuration releaseRuntimeClasspath`.

### Direct Dependencies

| Name | Version | Type | Location in APK | SBOM Detectable? | Notes |
|------|---------|------|-----------------|-------------------|-------|
| androidx.appcompat:appcompat | 1.6.1 | AAR | META-INF, classes.dex | Yes (pkg:maven PURL) | Direct — declared in build.gradle |

### Transitive Dependencies

| Name | Version | Type | Location in APK | SBOM Detectable? | Notes |
|------|---------|------|-----------------|-------------------|-------|
| androidx.activity:activity | 1.6.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.annotation:annotation | 1.3.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive (resolved version) |
| androidx.annotation:annotation-experimental | 1.3.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via core |
| androidx.appcompat:appcompat-resources | 1.6.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.arch.core:core-common | 2.1.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via lifecycle |
| androidx.arch.core:core-runtime | 2.1.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via lifecycle |
| androidx.collection:collection | 1.1.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via core/appcompat |
| androidx.concurrent:concurrent-futures | 1.0.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via core |
| androidx.core:core | 1.9.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.core:core-ktx | 1.9.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via viewmodel-savedstate |
| androidx.cursoradapter:cursoradapter | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.customview:customview | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via drawerlayout |
| androidx.drawerlayout:drawerlayout | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.emoji2:emoji2 | 1.2.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.emoji2:emoji2-views-helper | 1.2.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.fragment:fragment | 1.3.6 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.interpolator:interpolator | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via vectordrawable-animated |
| androidx.lifecycle:lifecycle-common | 2.5.1 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via lifecycle-runtime |
| androidx.lifecycle:lifecycle-livedata | 2.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via loader |
| androidx.lifecycle:lifecycle-livedata-core | 2.5.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via viewmodel-savedstate |
| androidx.lifecycle:lifecycle-process | 2.4.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via emoji2 |
| androidx.lifecycle:lifecycle-runtime | 2.5.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via activity/appcompat |
| androidx.lifecycle:lifecycle-viewmodel | 2.5.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via activity |
| androidx.lifecycle:lifecycle-viewmodel-savedstate | 2.5.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via activity |
| androidx.loader:loader | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via fragment |
| androidx.resourceinspection:resourceinspection-annotation | 1.0.1 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat |
| androidx.savedstate:savedstate | 1.2.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via activity |
| androidx.startup:startup-runtime | 1.1.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via lifecycle-process |
| androidx.tracing:tracing | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via activity |
| androidx.vectordrawable:vectordrawable | 1.1.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat-resources |
| androidx.vectordrawable:vectordrawable-animated | 1.1.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via appcompat-resources |
| androidx.versionedparcelable:versionedparcelable | 1.1.1 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via core |
| androidx.viewpager:viewpager | 1.0.0 | AAR | classes.dex | Yes (pkg:maven PURL) | Transitive via fragment |
| com.google.guava:listenablefuture | 1.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via concurrent-futures |
| org.jetbrains:annotations | 13.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via kotlin-stdlib |
| org.jetbrains.kotlin:kotlin-stdlib | 1.7.10 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via annotation-experimental |
| org.jetbrains.kotlin:kotlin-stdlib-common | 1.7.10 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via kotlin-stdlib |
| org.jetbrains.kotlin:kotlin-stdlib-jdk7 | 1.6.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via kotlin-stdlib-jdk8 |
| org.jetbrains.kotlin:kotlin-stdlib-jdk8 | 1.6.0 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via coroutines |
| org.jetbrains.kotlinx:kotlinx-coroutines-android | 1.6.1 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via viewmodel-savedstate |
| org.jetbrains.kotlinx:kotlinx-coroutines-bom | 1.6.1 | BOM (POM) | N/A (constraints only) | Yes (pkg:maven PURL) | BOM — no code, only version constraints |
| org.jetbrains.kotlinx:kotlinx-coroutines-core | 1.6.1 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via coroutines-android |
| org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm | 1.6.1 | JAR | classes.dex | Yes (pkg:maven PURL) | Transitive via coroutines-core |

## Local Dependencies

| Name | Version | Type | Location in APK | SBOM Detectable? | Notes |
|------|---------|------|-----------------|-------------------|-------|
| local-lib.jar (com.locallib.LocalUtils) | N/A | JAR | META-INF/MANIFEST.MF, classes.dex | No — no Maven coordinate, no pkg:maven PURL | Manually added via `implementation(files("libs/local-lib.jar"))`. SBOM tools relying on Gradle resolution or POM metadata will miss this entirely. |

## Native Dependencies

| Name | Version | Type | Location in APK | SBOM Detectable? | Notes |
|------|---------|------|-----------------|-------------------|-------|
| libnative-crypto.so | Unknown | .so (ELF arm64) | lib/arm64-v8a/libnative-crypto.so | Partial — ELF/LIEF analysis only | Prebuilt ARM64 shared library placed in jniLibs/arm64-v8a/. Not declared in Gradle, no Maven coordinate. Detectable only by tools that perform ELF binary analysis (e.g., Blint with LIEF). Invisible to Syft/CycloneDX Gradle plugin. |

## Dynamic Dependencies

| Name | Version | Type | Location in APK | SBOM Detectable? | Notes |
|------|---------|------|-----------------|-------------------|-------|
| dynamic-payload.dex (com.dynamic.payload.EntryPoint) | N/A | DEX | Not present at build time | No — runtime only, invisible to all static SBOM tools | Loaded at runtime via `DexClassLoader` from external storage (`/sdcard/Download/dynamic-payload.dex`). No build-time artifact exists. Only detectable via static analysis of the Java source (string literal / DexClassLoader usage pattern). |

## SBOM Detection Summary

| Dependency Type | Example in This APK | Syft | Blint | CycloneDX Gradle | Notes |
|----------------|---------------------|------|-------|-------------------|-------|
| Remote (Maven) | androidx.appcompat:appcompat:1.6.1 | Yes | Yes | Yes | Standard pkg:maven PURL from Gradle metadata |
| Local (JAR) | local-lib.jar | No | Partial (class scan) | No | No POM/Maven coordinate — no PURL possible |
| Native (.so) | libnative-crypto.so | No | Yes (ELF analysis) | No | Requires LIEF/ELF parsing of lib/ directory |
| Dynamic (DEX) | dynamic-payload.dex | No | No | No | Not present in APK — loaded at runtime only |

## Files Modified

| File | Change |
|------|--------|
| `app/build.gradle.kts` | Added `implementation(files("libs/local-lib.jar"))` |
| `app/libs/local-lib.jar` | New file — minimal JAR with `com.locallib.LocalUtils` class |
| `app/src/main/jniLibs/arm64-v8a/libnative-crypto.so` | New file — minimal ARM64 ELF shared library |
| `app/src/main/java/com/test/reachability/DynamicLoaderActivity.java` | New file — activity that loads DEX at runtime via `DexClassLoader` |
| `app/src/main/java/com/test/reachability/MainActivity.java` | Added "Dynamic Loader Demo" button |
| `app/src/main/AndroidManifest.xml` | Registered `DynamicLoaderActivity` |

## APK Structure (Expected)

```
app-release.apk
├── META-INF/
│   ├── MANIFEST.MF          ← includes local-lib.jar manifest entries
│   └── *.version             ← androidx version files from remote deps
├── classes.dex               ← all Java bytecode (remote + local + DynamicLoaderActivity)
├── lib/
│   └── arm64-v8a/
│       └── libnative-crypto.so   ← native prebuilt (ELF analysis target)
├── res/                      ← Android resources
├── resources.arsc            ← compiled resources
└── AndroidManifest.xml       ← includes DynamicLoaderActivity registration
```

Note: `dynamic-payload.dex` is NOT inside the APK — it is loaded from external storage at runtime.
