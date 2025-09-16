package com.sagarpaliwal_task.core.data

import com.sagarpaliwal_task.core.model.HoldingsResponse

interface HoldingsDataSource {
    suspend fun getHoldings(): Result<HoldingsResponse>
}
