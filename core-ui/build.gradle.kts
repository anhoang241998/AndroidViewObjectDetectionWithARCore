plugins {
    `android-library`
    `kotlin-android`
}

apply(from = "$rootDir/presentation-module.gradle")

android {
    namespace = "com.annguyenhoang.core_ui"
}

dependencies {
    coreModule()

    // DI - Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compat)
    implementation(libs.koin.androidx.navigation)

    // Object feature and model
    implementation("com.google.mlkit:object-detection:17.0.1")
    // Custom model
    implementation("com.google.mlkit:object-detection-custom:17.0.1")

    api("com.google.guava:guava:33.0.0-jre")
}