package com.dirzaaulia.yomiru

import android.app.Application
import com.dirzaaulia.yomiru.di.networkModule
import com.dirzaaulia.yomiru.di.repositoryModule
import com.dirzaaulia.yomiru.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class YomiruApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@YomiruApp)
            modules(
                networkModule,
                repositoryModule,
                viewModelModule
            )
        }
    }
}