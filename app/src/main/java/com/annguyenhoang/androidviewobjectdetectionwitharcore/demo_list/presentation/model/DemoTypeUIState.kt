package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.model

enum class DemoTypeUIState(val type: String) {
    YOLOv8("YOLOv8");

    companion object {
        fun toUIState(type: String): DemoTypeUIState {
            return DemoTypeUIState.entries.firstOrNull { uiStateType ->
                uiStateType.type == type
            } ?: YOLOv8
        }
    }
}