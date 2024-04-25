plugins {
    `android-library`
    `kotlin-android`
}

apply(from = "$rootDir/presentation-module.gradle")

android {
    namespace = "com.annguyenhoang.camerax_with_yolov8_presentation"
}

dependencies {

    coreModule()
    coreUiModule()
    cameraxWithYOLOV8DomainModule()

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
}