import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// local.properties dosyasından API anahtarlarını okuyoruz.
// Bu dosya asla Git'e eklenmemeli (.gitignore içinde zaten hariç tutulur).
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY")
    ?: System.getenv("GEMINI_API_KEY")
    ?: ""

android {
    namespace = "com.example.geminimapsassistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.geminimapsassistant"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Kotlin kodundan BuildConfig.GEMINI_API_KEY olarak erişilir
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.activity:activity-compose:1.9.0")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // OpenStreetMap (osmdroid) - ücretsiz, API key gerektirmeyen harita kütüphanesi
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    // Konum servisi için (bu, Maps API key'inden bağımsızdır, ücretsizdir)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Gemini (Google AI) SDK
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
