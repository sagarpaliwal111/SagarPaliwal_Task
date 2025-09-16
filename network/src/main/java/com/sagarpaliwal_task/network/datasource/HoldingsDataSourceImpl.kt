package com.sagarpaliwal_task.network.datasource

import com.sagarpaliwal_task.core.data.HoldingsDataSource
import com.sagarpaliwal_task.core.model.HoldingsResponse
import com.sagarpaliwal_task.network.api.HoldingsApiService

class HoldingsDataSourceImpl(
    private val holdingsApiService: HoldingsApiService
) : HoldingsDataSource {
    
    override suspend fun getHoldings(): Result<HoldingsResponse> {
        return try {
            val response = holdingsApiService.getHoldings()
            if (response.isSuccessful) {
                response.body()?.let { 
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
