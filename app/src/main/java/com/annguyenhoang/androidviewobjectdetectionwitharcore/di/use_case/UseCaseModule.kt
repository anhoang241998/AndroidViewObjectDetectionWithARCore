package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.use_case

import com.annguyenhoang.core.utils.KoinModule
import com.annguyenhoang.camerax_with_yolov8_domain.use_case.ObserveApplicationFPSUseCase
import com.annguyenhoang.demo_list_domain.use_case.GetDemoListUseCase
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