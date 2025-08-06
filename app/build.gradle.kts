// App-level build.gradle.kts

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.pack.uniflow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pack.uniflow"
        minSdk = 21
        targetSdk = 34
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
}

dependencies {
    // AndroidX Core Libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase BoM (version-managed dependencies)
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")

    // Glide (Image Loader)
    val glideVersion = "4.15.1"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor("com.github.bumptech.glide:compiler:$glideVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
}
