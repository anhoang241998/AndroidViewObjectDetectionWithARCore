import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

object Modules {
    const val core = ":core"
    const val coreUi = ":core-ui"

    // camerax with yolov8
    const val cameraxWithYOLOV8Presentation = ":camerax-with-yolov8:camerax-with-yolov8-presentation"
    const val cameraxWithYOLOV8Domain = ":camerax-with-yolov8:camerax-with-yolov8-domain"
    const val cameraxWithYOLOV8Data = ":camerax-with-yolov8:camerax-with-yolov8-data"
}

fun DependencyHandler.coreModule() {
    implementation(project(Modules.core))
}

fun DependencyHandler.coreUiModule() {
    implementation(project(Modules.coreUi))
}

fun DependencyHandler.cameraxWithYOLOV8PresentationModule() {
    implementation(project(Modules.cameraxWithYOLOV8Presentation))
}

fun DependencyHandler.cameraxWithYOLOV8DomainModule() {
    implementation(project(Modules.cameraxWithYOLOV8Domain))
}

fun DependencyHandler.cameraxWithYOLOV8DataModule() {
    implementation(project(Modules.cameraxWithYOLOV8Data))
}