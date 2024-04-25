// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.gradle.android.build.tools)
        classpath(libs.gradle.kotlin.plugins)
        classpath(libs.android.navigation.component.safeArgs)
    }
}