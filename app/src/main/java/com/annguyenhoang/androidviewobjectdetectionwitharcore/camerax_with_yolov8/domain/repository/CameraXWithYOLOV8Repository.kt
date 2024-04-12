package com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.repository

import com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.model.ApplicationFPS
import kotlinx.coroutines.flow.Flow

interface CameraXWithYOLOV8Repository {

    fun observeAppFps(): Flow<ApplicationFPS>

}