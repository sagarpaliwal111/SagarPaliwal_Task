package com.sagarpaliwal_task.di

import com.sagarpaliwal_task.core.data.HoldingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    
    single<HoldingsRepository> {
        HoldingsRepository(get())
    }
}