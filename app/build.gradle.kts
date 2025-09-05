plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id ("kotlin-kapt")  // This line is for enabling KAPT
}

android {
    namespace = "com.ragul.notetaking"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ragul.notetaking"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // navigation
    implementation ("androidx.navigation:navigation-compose:2.9.3")
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth:24.0.1")
    //room db
    // Room Database
    implementation ("androidx.room:room-runtime:2.5.2")  // Replace with the latest version
    implementation ("androidx.room:room-ktx:2.5.2")  // Kotlin Extensions for Room
    annotationProcessor ("androidx.room:room-compiler:2.5.2")  // For Java users
    kapt ("androidx.room:room-compiler:2.5.2")  // For Kotlin users
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.2")  // For lifecycle components if using ViewModel
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.2")  // For LiveData
    implementation ("androidx.compose.runtime:runtime-livedata:1.5.4")  // For LiveData observation in Compose

}