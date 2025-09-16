package com.sagarpaliwal_task.core.data

import com.sagarpaliwal_task.core.model.HoldingsResponse
import com.sagarpaliwal_task.core.util.Either
import com.sagarpaliwal_task.core.util.Failure

interface HoldingsDataSource {
    suspend fun getHoldings(): Either<Failure, HoldingsResponse>
}
