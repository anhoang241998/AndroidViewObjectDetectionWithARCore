package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local.model.DemoData
import kotlinx.coroutines.flow.flow

class LocalDataSource {

    private val demoItemDataSource = buildList {
        repeat(1) { index ->
            add(
                DemoData(
                    demoId = index,
                    demoName = "CameraX with YOLOv8",
                    demoType = CAMERAX_YOLO_V8
                )
            )
        }
    }

    fun observeDemoList() = flow {
        emit(demoItemDataSource)
    }

    companion object {
        private const val CAMERAX_YOLO_V8 = "CameraXAndYOLOv8"
    }

}