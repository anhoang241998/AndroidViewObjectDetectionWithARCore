package com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.use_case

import com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.repository.CameraXWithYOLOV8Repository
import kotlinx.coroutines.flow.map
import kotlin.math.round

class ObserveApplicationFPSUseCase(
    private val cameraXWithYOLOV8Repository: CameraXWithYOLOV8Repository
) {

    operator fun invoke() = cameraXWithYOLOV8Repository.observeAppFps()
        .map { applicationFPS ->
            val scale = 10.0
            applicationFPS.copy(
                fpsValue = round(applicationFPS.fpsValue * scale) / scale
            )
        }

}