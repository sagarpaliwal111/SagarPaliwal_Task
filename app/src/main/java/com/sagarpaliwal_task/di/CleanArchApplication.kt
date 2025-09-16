package com.sagarpaliwal_task.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CleanArchApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@CleanArchApplication)
            modules(
                networkModule,
                dataSourceModule,
                useCaseModule,
                viewModelModule,
                repositoryModule
            )
        }
    }

}