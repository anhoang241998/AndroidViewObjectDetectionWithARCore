package com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.use_case.ObserveApplicationFPSUseCase
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class CameraXWithYOLOV8ViewModel(
    observeApplicationFPSUseCase: ObserveApplicationFPSUseCase
) : ViewModel() {

    val observeApplicationFPSUseCase: SharedFlow<Double> = observeApplicationFPSUseCase()
        .map { applicationFPS ->
            applicationFPS.fpsValue
        }
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(300)
        )

}