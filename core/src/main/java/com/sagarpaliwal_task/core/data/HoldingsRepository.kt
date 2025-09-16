package com.sagarpaliwal_task.core.data

import com.sagarpaliwal_task.core.model.HoldingsResponse
import com.sagarpaliwal_task.core.util.Either
import com.sagarpaliwal_task.core.util.Failure

class HoldingsRepository(
    private val holdingsDataSource: HoldingsDataSource
) {
    suspend fun getHoldings(): Either<Failure, HoldingsResponse> {
        return holdingsDataSource.getHoldings()
    }
}

