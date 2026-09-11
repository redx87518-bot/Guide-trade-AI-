package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PaperTradeData(
    val id: String = "",
    val orderId: String = "",
    val userId: String = "",
    val symbol: String = "",
    val side: String = "",
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val notionalValue: Double = 0.0,
    val createdAt: String = "",
)
