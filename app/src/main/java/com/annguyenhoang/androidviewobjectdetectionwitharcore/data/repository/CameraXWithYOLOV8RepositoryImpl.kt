package com.annguyenhoang.androidviewobjectdetectionwitharcore.data.repository

import com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.model.ApplicationFPS
import com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.repository.CameraXWithYOLOV8Repository
import com.annguyenhoang.androidviewobjectdetectionwitharcore.data.data_source.AppLocalEventDataSource
import com.annguyenhoang.androidviewobjectdetectionwitharcore.data.model.AppLocalEventType
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