package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.view_model

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.presentation.DemoListViewModel
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object ViewModelModule : KoinModule {
    override val module: Module
        get() = module {
            viewModelOf(::DemoListViewModel)
        }
}