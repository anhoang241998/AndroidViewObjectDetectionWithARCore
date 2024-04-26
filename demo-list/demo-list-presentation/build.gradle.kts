plugins {
    `android-library`
    `kotlin-android`
}

apply(from = "$rootDir/presentation-module.gradle")

android {
    namespace = "com.annguyenhoang.demo_list_presentation"
}

dependencies {
    coreModule()
    coreUiModule()
    demoListDomainModule()
}