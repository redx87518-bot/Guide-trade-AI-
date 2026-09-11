package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PaperPositionData(
    val id: String = "",
    val userId: String = "",
    val symbol: String = "",
    val quantity: Double = 0.0,
    val avgEntry: Double = 0.0,
    val currentPrice: Double = 0.0,
    val marketValue: Double = 0.0,
    val unrealizedPnl: Double = 0.0,
    val unrealizedPnlPercent: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = "",
)
