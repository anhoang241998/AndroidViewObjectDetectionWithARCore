package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.view_model

import com.annguyenhoang.core.utils.KoinModule
import com.annguyenhoang.mlkit_object_detection_presentation.MLKitObjectDetectionViewModel
import com.annguyenhoang.demo_list_presentation.DemoListViewModel
import com.annguyenhoang.presentation.CameraXWithYOLOV8ViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModule : KoinModule {
    override val module: Module
        get() = module {
            viewModelOf(::DemoListViewModel)
            viewModelOf(::CameraXWithYOLOV8ViewModel)
            viewModelOf(::MLKitObjectDetectionViewModel)
        }
}