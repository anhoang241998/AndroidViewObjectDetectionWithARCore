package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.common

import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
import com.annguyenhoang.core_ui.permission.CameraPermissionHandlerImpl
import com.annguyenhoang.core_ui.permission.PermissionHandler
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.dsl.module

object PermissionModule : KoinModule {
    override val module: Module
        get() = module {
            factoryOf(::CameraPermissionHandlerImpl) {
                bind<PermissionHandler>()
                named<CameraPermissionHandlerImpl>()
            }
        }
}