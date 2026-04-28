plugins {
    id("com.android.application")
}

android {
    namespace = "com.test.reachability"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.test.reachability"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")

    // LOCAL DEPENDENCY: No Maven coordinates — invisible to SBOM tools that rely on pkg:maven PURLs.
    // Will appear in META-INF and classes.dex but cannot be resolved to a PURL.
    implementation(files("libs/local-lib.jar"))
}