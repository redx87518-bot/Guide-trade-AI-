package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PaperAccountData(
    val id: String = "",
    val userId: String = "",
    val balance: Double = 0.0,
    val equity: Double = 0.0,
    val buyingPower: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = "",
)
