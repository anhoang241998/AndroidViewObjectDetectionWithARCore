package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local.model.DemoData
import kotlinx.coroutines.flow.flow

class LocalDataSource {

    private val demoItemDataSource = listOf(
        DemoData(
            demoId = 1,
            demoName = "Object Detection With MLKit",
            demoType = MLKIT_OBJECT_DETECTION
        ),
        DemoData(
            demoId = 2,
            demoName = "CameraX with YOLOv8",
            demoType = CAMERAX_YOLO_V8
        )
    )

    fun observeDemoList() = flow {
        emit(demoItemDataSource)
    }

    companion object {
        private const val MLKIT_OBJECT_DETECTION = "MLKitObjectDetection"
        private const val CAMERAX_YOLO_V8 = "CameraXAndYOLOv8"
    }

}