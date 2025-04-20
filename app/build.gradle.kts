// Make sure this plugins block is at the VERY TOP of the file
plugins {
    // This alias applies the Android Application plugin, which defines implementation, etc.
    alias(libs.plugins.android.application)

    // If using Kotlin annotation processing for Room/Glide, add:
    // id("kotlin-kapt") // Or use alias(libs.plugins.kotlin.kapt) if defined
    // Or if using KSP:
    // id("com.google.devtools.ksp") version "..." // Add KSP plugin if using it
}

android {
    namespace = "com.pack.uniflow"
    compileSdk = 35 // Consider using the latest stable SDK if 35 is alpha/beta

    defaultConfig {
        applicationId = "com.pack.uniflow"
        minSdk = 21
        targetSdk = 35 // Consider using the latest stable SDK if 35 is alpha/beta
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // Add if using Kotlin
    // kotlinOptions {
    //    jvmTarget = "11"
    // }
}

// The dependencies block uses the configurations defined by the plugin
dependencies {

    // Core App Dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Room Dependencies
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // kapt("androidx.room:room-compiler:$room_version") // Use if using kapt plugin
    // ksp("androidx.room:room-compiler:$room_version") // Use if using ksp plugin

    // Glide Dependencies
    val glide_version = "4.15.1"
    implementation("com.github.bumptech.glide:glide:$glide_version")
    annotationProcessor("com.github.bumptech.glide:compiler:$glide_version")
    // kapt("com.github.bumptech.glide:compiler:$glide_version") // Use if using kapt plugin
    // ksp("com.github.bumptech.glide:compiler:$glide_version") // Use if using ksp plugin


    // --- Testing Dependencies ---

    // Local Unit Tests (JUnit)
    testImplementation(libs.junit)

    // Instrumented Tests (AndroidX Test, Espresso)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)

    // Espresso Contrib
    val espresso_version = "3.5.1"
    androidTestImplementation("androidx.test.espresso:espresso-contrib:$espresso_version")

    // Room Testing
    androidTestImplementation("androidx.room:room-testing:$room_version")

    // Test Runner and Rules
    val test_runner_version = "1.5.2"
    val test_rules_version = "1.5.0"
    androidTestImplementation("androidx.test:runner:$test_runner_version")
    androidTestImplementation("androidx.test:rules:$test_rules_version")

    // Fragment Testing
    val fragment_version = "1.6.2"
    debugImplementation("androidx.fragment:fragment-testing:$fragment_version")

}