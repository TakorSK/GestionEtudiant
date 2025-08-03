// Project-level build.gradle.kts

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Plugin for Firebase services like google-services.json
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    // These are applied in the app module
    id("com.android.application") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false // Can be removed if not using Kotlin
}
