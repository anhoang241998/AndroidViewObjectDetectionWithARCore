package com.annguyenhoang.demo_list_domain.model

enum class DemoType(val type: String) {
    CAMERAX_WITH_YOLO_V8("CameraXAndYOLOv8"),
    MLKIT_OBJECT_DETECTION("MLKitObjectDetection");

    companion object {
        fun toDomain(type: String): DemoType {
            return DemoType.entries.firstOrNull { demoType ->
                demoType.type == type
            } ?: CAMERAX_WITH_YOLO_V8
        }
    }

}