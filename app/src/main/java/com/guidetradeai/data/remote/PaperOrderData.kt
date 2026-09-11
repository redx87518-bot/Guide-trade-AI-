package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PaperOrderData(
    val id: String = "",
    val userId: String = "",
    val symbol: String = "",
    val side: String = "",
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val notionalValue: Double = 0.0,
    val orderType: String = "",
    val status: String = "",
    val createdAt: String = "",
)
