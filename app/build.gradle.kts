plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.2.0"
}

android {
    namespace = "com.example.thread"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.thread"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "CLOUDINARY_CLOUD_NAME",
            "\"${project.findProperty("CLOUDINARY_CLOUD_NAME")}\""
        )
        buildConfigField(
            "String",
            "CLOUDINARY_API_KEY",
            "\"${project.findProperty("CLOUDINARY_API_KEY")}\""
        )

    }

    buildTypes {
        release {
            //     isMinifyEnabled = true
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose) // ✅ keep BOM version
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)

    // Media
    implementation(libs.play.services.cast.tv)
    implementation(libs.androidx.media3.common.ktx)
    // Firebase Messaging
    implementation(libs.firebase.messaging)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation
    val nav_version = "2.8.9"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // ConstraintLayout for Compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // Firebase (using BOM)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Coil (Image Loading)
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Cloudinary (Image Hosting)
    implementation("com.cloudinary:cloudinary-android:3.1.1")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")


    // Shimmer placeholder
    implementation("com.google.accompanist:accompanist-placeholder-material:0.36.0")
    // Pull-to-refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.36.0")


    // Firebase messaging runtime permission
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")


    //  firebase messaging to send message from own mobile
    implementation("com.google.firebase:firebase-functions-ktx:21.2.1")

}
