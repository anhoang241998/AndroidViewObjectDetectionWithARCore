package com.annguyenhoang.androidviewobjectdetectionwitharcore.di.common

import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.permission.CameraPermissionHandlerImpl
import com.annguyenhoang.androidviewobjectdetectionwitharcore.common.permission.PermissionHandler
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.KoinModule
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