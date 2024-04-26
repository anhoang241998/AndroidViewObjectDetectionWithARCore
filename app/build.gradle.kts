plugins {
    id(libs.plugins.androidApplication.get().pluginId)
    id(libs.plugins.jetbrainsKotlinAndroid.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.android.navigation.safeargs.get().pluginId)
}

android {
    namespace = "com.annguyenhoang.androidviewobjectdetectionwitharcore"
    compileSdk = ProjectConfig.COMPILE_SDK

    defaultConfig {
        applicationId = ProjectConfig.APP_ID
        minSdk = ProjectConfig.MIN_SDK
        targetSdk = ProjectConfig.TARGET_SDK
        versionCode = ProjectConfig.VERSION_CODE
        versionName = ProjectConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    androidResources {
        noCompress += "tflite"
    }
}

dependencies {

    // Modules
    coreModule()
    coreUiModule()
    cameraxWithYOLOV8PresentationModule()
    cameraxWithYOLOV8DomainModule()
    cameraxWithYOLOV8DataModule()
    demoListPresentationModule()
    demoListDomainModule()
    demoListDataModule()
    mlKitObjetDetectionPresentationModule()

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // coroutine
    implementation(libs.kotlin.coroutine.core)
    implementation(libs.kotlin.coroutine.android)

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

    // FPS
    debugImplementation(libs.takt.fps.log)
}