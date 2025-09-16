package com.sagarpaliwal_task.di

import com.sagarpaliwal_task.core.data.HoldingsDataSource
import com.sagarpaliwal_task.network.datasource.HoldingsDataSourceImpl
import org.koin.dsl.module

val dataSourceModule = module {
    
    single<HoldingsDataSource> {
        HoldingsDataSourceImpl(get())
    }
}