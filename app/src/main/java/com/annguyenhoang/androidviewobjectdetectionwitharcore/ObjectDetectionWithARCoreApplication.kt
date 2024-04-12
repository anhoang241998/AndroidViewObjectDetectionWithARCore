package com.annguyenhoang.androidviewobjectdetectionwitharcore

import android.app.Application
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.data_source.DataSourceModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.navigation.NavigationModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.repository.RepositoryModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.use_case.UseCaseModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.view_model.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import timber.log.Timber.DebugTree

class ObjectDetectionWithARCoreApplication : Application() {

    private val modules = listOf(
        NavigationModule.module,
        DataSourceModule.module,
        RepositoryModule.module,
        UseCaseModule.module,
        ViewModelModule.module
    )

    override fun onCreate() {
        super.onCreate()
        configTimber()
        configKoin()
    }

    private fun configKoin() {
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@ObjectDetectionWithARCoreApplication)
            modules(modules)
        }
    }

    private fun configTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}