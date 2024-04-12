package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local.model.DemoData
import kotlinx.coroutines.flow.flow

class LocalDataSource {

    private val demoItemDataSource = buildList {
        repeat(1) { index ->
            add(
                DemoData(
                    demoId = index,
                    demoName = YOLO_V8,
                    demoType = YOLO_V8
                )
            )
        }
    }

    fun observeDemoList() = flow {
        emit(demoItemDataSource)
    }

    companion object {
        private const val YOLO_V8 = "YOLOv8"
    }

}