package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.model

enum class DemoType(val type: String) {
    YOLOv8("YOLOv8");

    companion object {
        fun toDomain(type: String): DemoType {
            return DemoType.entries.firstOrNull { demoType ->
                demoType.type == type
            } ?: YOLOv8
        }
    }

}