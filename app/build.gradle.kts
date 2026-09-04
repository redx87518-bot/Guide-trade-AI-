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

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-test-junit4")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Pager
    implementation("androidx.compose.foundation:foundation")

    // Supabase Kotlin SDK
    // Use -android suffix (release-only AARs) to avoid KMP variant matching issues.
    // Exclude base KMP modules (supabase-kt, gotrue-kt, etc.) from all transitive
    // deps to prevent available-at redirect from pulling both debug+release AARs.
    // Each -android module is added directly; base modules are redundant since their
    // classes are included in the corresponding -android AAR.
    implementation("io.github.jan-tennert.supabase:supabase-kt-android:2.6.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt-android:2.6.1") {
        exclude(group = "io.github.jan-tennert.supabase", module = "supabase-kt")
        exclude(group = "io.github.jan-tennert.supabase", module = "gotrue-kt")
    }
    implementation("io.github.jan-tennert.supabase:postgrest-kt-android:2.6.1") {
        exclude(group = "io.github.jan-tennert.supabase", module = "supabase-kt")
        exclude(group = "io.github.jan-tennert.supabase", module = "postgrest-kt")
    }
    implementation("io.github.jan-tennert.supabase:functions-kt-android:2.6.1") {
        exclude(group = "io.github.jan-tennert.supabase", module = "supabase-kt")
        exclude(group = "io.github.jan-tennert.supabase", module = "functions-kt")
    }
    implementation("io.github.jan-tennert.supabase:storage-kt-android:2.6.1") {
        exclude(group = "io.github.jan-tennert.supabase", module = "supabase-kt")
        exclude(group = "io.github.jan-tennert.supabase", module = "storage-kt")
    }
    implementation("io.github.jan-tennert.supabase:realtime-kt-android:2.6.1") {
        exclude(group = "io.github.jan-tennert.supabase", module = "supabase-kt")
        exclude(group = "io.github.jan-tennert.supabase", module = "realtime-kt")
    }

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
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
