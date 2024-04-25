plugins {
    `android-library`
    `kotlin-android`
}

apply(from = "$rootDir/base-module.gradle")

android {
    namespace = "com.annguyenhoang.camerax_with_yolov8_data"
}

dependencies {
    coreModule()
    cameraxWithYOLOV8DomainModule()
}