package com.sagarpaliwal_task.core.domain

import com.sagarpaliwal_task.core.data.HoldingsRepository
import com.sagarpaliwal_task.core.model.HoldingsResponse
import com.sagarpaliwal_task.core.util.Either
import com.sagarpaliwal_task.core.util.Failure

class GetHoldingsUseCase(
    private val holdingsRepository: HoldingsRepository
) {
    suspend operator fun invoke(): Either<Failure, HoldingsResponse> {
        return holdingsRepository.getHoldings()
    }
}

