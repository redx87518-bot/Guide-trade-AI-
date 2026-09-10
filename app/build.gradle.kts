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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.0"
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
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
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
    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.compose.foundation:foundation-android")
    implementation("androidx.compose.foundation:foundation-layout-android")
    implementation("io.github.jan-tennert.supabase:supabase-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:functions-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:storage-kt:2.6.1")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.6.1")
    implementation("io.ktor:ktor-client-android:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
