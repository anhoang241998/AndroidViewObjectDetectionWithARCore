pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidViewObjectDetectionWithARCore"
include(":app")
include(":core")
include(":core-ui")
include(":camerax-with-yolov8")
include(":camerax-with-yolov8:camerax-with-yolov8-presentation")
include(":camerax-with-yolov8:camerax-with-yolov8-domain")
include(":camerax-with-yolov8:camerax-with-yolov8-data")
