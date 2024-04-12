package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.repository

import com.annguyenhoang.androidviewobjectdetectionwitharcore.camerax_with_yolov8.domain.repository.CameraXWithYOLOV8Repository
import com.annguyenhoang.androidviewobjectdetectionwitharcore.data.repository.CameraXWithYOLOV8RepositoryImpl
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.repository.DemoListRepositoryImpl
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.repository.DemoListRepository
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
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