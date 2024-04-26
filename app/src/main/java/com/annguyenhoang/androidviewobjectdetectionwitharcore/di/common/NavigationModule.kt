package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.common

import com.annguyenhoang.androidviewobjectdetectionwitharcore.navigation.AppNavigatorImpl
import com.annguyenhoang.core.utils.KoinModule
import com.annguyenhoang.core_ui.navigation.AppNavigator
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object NavigationModule : KoinModule {
    override val module: Module
        get() = module {
            singleOf(::AppNavigatorImpl) {
                bind<AppNavigator>()
            }
        }
}