package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.use_case

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.use_case.GetDemoListUseCase
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
import com.annguyenhoang.camerax_with_yolov8_domain.use_case.ObserveApplicationFPSUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

object UseCaseModule : KoinModule {
    override val module: Module
        get() = module {
            factoryOf(::GetDemoListUseCase)
            factoryOf(::ObserveApplicationFPSUseCase)
        }

}