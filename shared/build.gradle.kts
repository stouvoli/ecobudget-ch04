plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    // Cible Android
    androidTarget {
        compilations.all { kotlinOptions.jvmTarget = "17" }
    }

    // Cibles iOS avec génération du framework binaire
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    // Dépendances communes
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.8.0")
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
        }
    }
}

// Configuration minimale requise par le plugin com.android.library
android {
    namespace = "com.example.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}