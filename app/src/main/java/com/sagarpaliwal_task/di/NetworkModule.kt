package com.sagarpaliwal_task.di

import com.sagarpaliwal_task.core.data.HoldingsDataSource
import com.sagarpaliwal_task.network.api.HoldingsApiService
import com.sagarpaliwal_task.network.datasource.HoldingsDataSourceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    single {
        Retrofit.Builder()
            .baseUrl("https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    single<HoldingsApiService> {
        get<Retrofit>().create(HoldingsApiService::class.java)
    }
    
    single<HoldingsDataSource> {
        HoldingsDataSourceImpl(get())
    }
}
