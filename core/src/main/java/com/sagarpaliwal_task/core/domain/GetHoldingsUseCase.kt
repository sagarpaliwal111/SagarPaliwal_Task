package com.sagarpaliwal_task.core.domain

import com.sagarpaliwal_task.core.data.HoldingsRepository
import com.sagarpaliwal_task.core.model.HoldingsResponse

class GetHoldingsUseCase(
    private val holdingsRepository: HoldingsRepository
) {
    suspend operator fun invoke(): Result<HoldingsResponse> {
        return holdingsRepository.getHoldings()
    }
}

