plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.annguyenhoang.androidviewobjectdetectionwitharcore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.annguyenhoang.androidviewobjectdetectionwitharcore"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // navigation component
    implementation(libs.android.navigation.component)
    implementation(libs.android.navigation.component.ui)
    implementation(libs.android.navigation.component.dynamicsFeature)
    androidTestImplementation(libs.android.navigation.component.testing)

    // DI - Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)
    implementation(libs.koin.androidx.navigation)

    // Timber
    implementation(libs.timber.log)
}