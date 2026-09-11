package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PaperOrderData(
    val id: String = "",
    val user_id: String = "",
    val symbol: String = "",
    val side: String = "buy",
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val status: String = "pending",
    val created_at: String = "",
)

@Serializable
data class PaperPositionData(
    val id: String = "",
    val user_id: String = "",
    val symbol: String = "",
    val quantity: Double = 0.0,
    val avg_entry_price: Double = 0.0,
    val current_price: Double = 0.0,
    val unrealized_pnl: Double = 0.0,
    val unrealized_pnl_percent: Double = 0.0,
    val created_at: String = "",
    val updated_at: String = "",
)

@Serializable
data class PaperTradeData(
    val id: String = "",
    val user_id: String = "",
    val symbol: String = "",
    val side: String = "buy",
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val notional_value: Double = 0.0,
    val created_at: String = "",
)

@Serializable
data class McpConnectionData(
    val id: String = "",
    val user_id: String = "",
    val name: String = "",
    val server_url: String = "",
    val api_key: String? = null,
    val enabled: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
)
