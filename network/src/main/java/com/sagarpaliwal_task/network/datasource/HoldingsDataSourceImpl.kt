package com.sagarpaliwal_task.network.datasource

import com.sagarpaliwal_task.core.data.HoldingsDataSource
import com.sagarpaliwal_task.core.model.HoldingsResponse
import com.sagarpaliwal_task.core.util.Either
import com.sagarpaliwal_task.core.util.Failure
import com.sagarpaliwal_task.network.api.HoldingsApiService

class HoldingsDataSourceImpl(
    private val holdingsApiService: HoldingsApiService
) : HoldingsDataSource {
    
    override suspend fun getHoldings(): Either<Failure, HoldingsResponse> {
        return try {
            val response = holdingsApiService.getHoldings()
            if (response.isSuccessful) {
                response.body()?.let { 
                    Either.Right(it)
                } ?: Either.Left(Failure.UnknownError("Empty response body"))
            } else {
                Either.Left(Failure.UnknownError("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Either.Left(Failure.NetworkConnection)
        }
    }
}
