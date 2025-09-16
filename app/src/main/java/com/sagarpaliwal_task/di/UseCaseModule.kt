package com.sagarpaliwal_task.di

import com.sagarpaliwal_task.core.domain.GetHoldingsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    
    single<GetHoldingsUseCase> {
        GetHoldingsUseCase(get())
    }
}