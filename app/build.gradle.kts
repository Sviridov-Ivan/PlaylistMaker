plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    kotlin("kapt") // для внедрения Room sp21

    id("com.google.gms.google-services") // firebase

}

android {
    namespace = "com.example.playlistmaker"
    compileSdk = 35

    buildFeatures {
        viewBinding = true // binding
    }

    defaultConfig {
        applicationId = "com.example.playlistmaker"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("playlistmaker-real.jks")
            storePassword = "MyPlay"
            keyAlias = "playlistmaker_alias"
            keyPassword = "Key123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // для активации оптимизации и шифровки перед релизом по умолчанию был false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug" // позволит иметь на устройстве релизную версию и дебаг версию для отладки
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.viewpager2)

    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.koin.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Корутины
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Room с корутинами сразу sp21
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // Опционально — Kotlin Extensions + Flow sp21
    implementation("androidx.room:room-ktx:$room_version")

    // firebase analytics
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")

}


