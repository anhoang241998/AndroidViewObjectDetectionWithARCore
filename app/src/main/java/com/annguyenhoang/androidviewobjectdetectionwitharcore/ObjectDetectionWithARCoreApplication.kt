package com.annguyenhoang.androidviewobjectdetectionwitharcore

import android.app.Application
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.common.PermissionModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.data_source.DataSourceModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.repository.RepositoryModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.use_case.UseCaseModule
import com.annguyenhoang.androidviewobjectdetectionwitharcore.di.view_model.ViewModelModule
import com.annguyenhoang.core.data.data_source.AppLocalEventDataSource
import com.annguyenhoang.core.data.model.AppLocalEventData
import com.annguyenhoang.core.data.model.AppLocalEventType
import jp.wasabeef.takt.Takt
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import timber.log.Timber.DebugTree

class ObjectDetectionWithARCoreApplication : Application() {

    private val appLocalEventDataSource: AppLocalEventDataSource by inject()

    private val modules = listOf(
        DataSourceModule.module,
        RepositoryModule.module,
        UseCaseModule.module,
        ViewModelModule.module,
        PermissionModule.module
    )

    override fun onCreate() {
        super.onCreate()
        configTimber()
        configKoin()

        Takt.stock(this)
            .hide()
            .listener { fpsValue ->
                appLocalEventDataSource.emitLocalEvent(
                    AppLocalEventData(
                        eventType = AppLocalEventType.FPS_TRACKING,
                        data = fpsValue
                    )
                )
            }
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