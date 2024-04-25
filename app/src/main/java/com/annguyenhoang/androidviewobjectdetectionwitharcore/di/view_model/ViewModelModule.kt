package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.view_model

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.DemoListViewModel
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.mlkit_object_detection.presentation.MLKitObjectDetectionViewModel
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