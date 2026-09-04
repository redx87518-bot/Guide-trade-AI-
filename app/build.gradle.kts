plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.guidetradeai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.guidetradeai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }

    packaging {
        resources {
            excludes += listOf("/META-INF/{AL2.0,LGPL2.1}")
        }
    }

    lint {
        disable.add("ParcelCreator")
        disable.add("UnusedIds")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Jetpack Compose - use -android variants because base artifacts have broken
    // Gradle module metadata (no files in android variants) on Google Maven
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.compose.runtime:runtime-android")
    implementation("androidx.compose.runtime:runtime-saveable-android")
    implementation("androidx.compose.ui:ui-android")
    implementation("androidx.compose.ui:ui-graphics-android")
    implementation("androidx.compose.ui:ui-tooling-preview-android")
    implementation("androidx.compose.material3:material3-android")
    implementation("androidx.compose.material:material-icons-core-android")
    implementation("androidx.compose.material:material-icons-extended-android")
    debugImplementation("androidx.compose.ui:ui-tooling-android")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-test-junit4-android")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Pager
    implementation("androidx.compose.foundation:foundation-android")
    implementation("androidx.compose.foundation:foundation-layout-android")

    // Supabase Kotlin SDK
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:functions-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.6.1")

    // Ktor (required by Supabase SDK for Android)
    implementation("io.ktor:ktor-client-android:2.3.12")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android")
}
