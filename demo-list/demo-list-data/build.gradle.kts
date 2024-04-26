plugins {
    `android-library`
    `kotlin-android`
}

apply(from = "$rootDir/base-module.gradle")

android {
    namespace = "com.annguyenhoang.demo_list_data"
}

dependencies {
    coreModule()
    demoListDomainModule()
}