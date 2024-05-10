plugins {
    `android-library`
    `kotlin-android`
}

apply(from = "$rootDir/presentation-module.gradle")

android {
    namespace = "com.annguyenhoang.mlkit_object_detection_presentation"
}

dependencies {
    coreModule()
    coreUiModule()

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifeCycle)
    implementation(libs.camerax.video)
    implementation(libs.camerax.view)
    implementation(libs.camerax.ext)

    // ML Kit
    implementation(libs.mlkit.objectdetection)
    implementation(libs.mlkit.objectdetection.custom)

    // Coil
    implementation(libs.coil.kotlin)

    implementation("com.android.volley:volley:1.2.1")
}