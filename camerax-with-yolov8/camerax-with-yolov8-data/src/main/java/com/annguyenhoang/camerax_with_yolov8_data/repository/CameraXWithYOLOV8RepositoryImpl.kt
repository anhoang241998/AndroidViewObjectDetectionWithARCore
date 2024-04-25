package com.annguyenhoang.camerax_with_yolov8_data.repository

import com.annguyenhoang.camerax_with_yolov8_domain.model.ApplicationFPS
import com.annguyenhoang.camerax_with_yolov8_domain.repository.CameraXWithYOLOV8Repository
import com.annguyenhoang.core.data.data_source.AppLocalEventDataSource
import com.annguyenhoang.core.data.model.AppLocalEventType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CameraXWithYOLOV8RepositoryImpl(
    private val localAppLocalEventDataSource: AppLocalEventDataSource
) : CameraXWithYOLOV8Repository {
    override fun observeAppFps(): Flow<ApplicationFPS> {
        return localAppLocalEventDataSource.event.map { localEvent ->
            if (localEvent.eventType == AppLocalEventType.FPS_TRACKING) {
                ApplicationFPS(localEvent.data as Double)
            } else {
                ApplicationFPS(0.0)
            }
        }
    }
}