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
}