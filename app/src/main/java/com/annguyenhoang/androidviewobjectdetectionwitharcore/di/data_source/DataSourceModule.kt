package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.data_source

import com.annguyenhoang.core.utils.KoinModule
import com.annguyenhoang.core.data.data_source.AppLocalEventDataSource
import com.annguyenhoang.demo_list_data.local.LocalDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object DataSourceModule : KoinModule {
    override val module: Module
        get() = module {
            singleOf(::LocalDataSource)
            singleOf(::AppLocalEventDataSource)
        }
}