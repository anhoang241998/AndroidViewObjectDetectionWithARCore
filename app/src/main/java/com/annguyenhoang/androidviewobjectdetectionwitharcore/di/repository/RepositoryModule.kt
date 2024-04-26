package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.repository

import com.annguyenhoang.core.utils.KoinModule
import com.annguyenhoang.camerax_with_yolov8_data.repository.CameraXWithYOLOV8RepositoryImpl
import com.annguyenhoang.camerax_with_yolov8_domain.repository.CameraXWithYOLOV8Repository
import com.annguyenhoang.demo_list_data.repository.DemoListRepositoryImpl
import com.annguyenhoang.demo_list_domain.repository.DemoListRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

object RepositoryModule : KoinModule {
    override val module: Module
        get() = module {
            factoryOf(::DemoListRepositoryImpl) {
                bind<DemoListRepository>()
            }

            factoryOf(::CameraXWithYOLOV8RepositoryImpl) {
                bind<CameraXWithYOLOV8Repository>()
            }
        }

}