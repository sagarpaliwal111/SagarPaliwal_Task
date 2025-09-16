package com.sagarpaliwal_task.di

import com.sagarpaliwal_task.presentation.HoldingsViewModel
import com.sagarpaliwal_task.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    
    viewModel<MainViewModel> {
        MainViewModel(get())
    }
    
    viewModel<HoldingsViewModel> {
        HoldingsViewModel(get())
    }
}