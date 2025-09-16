package com.sagarpaliwal_task.di

import android.app.Application
import com.sagarpaliwal_task.di.dataSourceModule
import com.sagarpaliwal_task.di.repositoryModule
import com.sagarpaliwal_task.di.useCaseModule
import com.sagarpaliwal_task.di.viewModelModule
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