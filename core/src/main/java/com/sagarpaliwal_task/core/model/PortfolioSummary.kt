package com.sagarpaliwal_task.core.model

data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnL: Double,
    val totalPnLPercentage: Double,
    val todaysPnL: Double
)
