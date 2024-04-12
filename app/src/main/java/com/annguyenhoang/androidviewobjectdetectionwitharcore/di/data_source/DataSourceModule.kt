package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.data_source

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local.LocalDataSource
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object DataSourceModule : KoinModule {
    override val module: Module
        get() = module {
            singleOf(::LocalDataSource)
        }
}