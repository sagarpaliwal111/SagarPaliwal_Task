package com.sagarpaliwal_task.network.api

import com.sagarpaliwal_task.core.model.HoldingsResponse
import retrofit2.Response
import retrofit2.http.GET

interface HoldingsApiService {
    @GET(".")
    suspend fun getHoldings(): Response<HoldingsResponse>
}

