package com.annguyenhoang.androidviewobjectdetectionwitharcore

import android.app.Application
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.navigation.NavigationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class ObjectDetectionWithARCoreApplication : Application() {

    private val modules = listOf(
        NavigationModule.module
    )

    override fun onCreate() {
        super.onCreate()
        configKoin()
    }

    private fun configKoin() {
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@ObjectDetectionWithARCoreApplication)
            modules(modules)
        }
    }

}