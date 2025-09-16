package com.sagarpaliwal_task.core.data

import com.sagarpaliwal_task.core.model.HoldingsResponse

class HoldingsRepository(
    private val holdingsDataSource: HoldingsDataSource
) {
    suspend fun getHoldings(): Result<HoldingsResponse> {
        return holdingsDataSource.getHoldings()
    }
}

