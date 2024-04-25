package com.annguyenhoang.camerax_with_yolov8_domain.repository

import com.annguyenhoang.camerax_with_yolov8_domain.model.ApplicationFPS
import kotlinx.coroutines.flow.Flow

interface CameraXWithYOLOV8Repository {

    fun observeAppFps(): Flow<ApplicationFPS>

}