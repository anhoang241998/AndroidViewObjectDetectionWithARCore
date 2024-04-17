package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.model

enum class DemoTypeUIState(val type: String) {
    CAMERAX_WITH_YOLO_V8("CameraXAndYOLOv8"),
    MLKIT_OBJECT_DETECTION("MLKitObjectDetection");

    companion object {
        fun toUIState(type: String): DemoTypeUIState {
            return DemoTypeUIState.entries.firstOrNull { uiStateType ->
                uiStateType.type == type
            } ?: CAMERAX_WITH_YOLO_V8
        }
    }
}