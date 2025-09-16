package com.sagarpaliwal_task.core.model

import com.google.gson.annotations.SerializedName

data class HoldingsData(
    @SerializedName("userHolding")
    val userHolding: List<UserHolding>
)

data class HoldingsResponse(
    @SerializedName("data")
    val data: HoldingsData
)
