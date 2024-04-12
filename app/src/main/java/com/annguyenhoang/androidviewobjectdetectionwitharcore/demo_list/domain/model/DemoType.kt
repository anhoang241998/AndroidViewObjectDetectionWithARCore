package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.model

enum class DemoType(val type: String) {
    CAMERAX_WITH_YOLO_V8("CameraXAndYOLOv8");

    companion object {
        fun toDomain(type: String): DemoType {
            return DemoType.entries.firstOrNull { demoType ->
                demoType.type == type
            } ?: CAMERAX_WITH_YOLO_V8
        }
    }

}