package com.guidetradeai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val voiceEnabled: Boolean = true,
    val autoSpeak: Boolean = false,
    val theme: String = "dark",
)

@Serializable
data class ChatMessage(
    val id: String = "",
    val sessionId: String = "",
    val role: String = "user",
    val content: String = "",
    val createdAt: String = "",
)

@Serializable
data class ChatSession(
    val id: String = "",
    val userId: String = "",
    val title: String = "New Chat",
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class ResearchResult(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val query: String = "",
    val response: String = "",
    val asset: String? = null,
    val createdAt: String = "",
)

@Serializable
data class TelegramSettings(
    val botTokenEncrypted: String? = null,
    val chatId: String? = null,
    val enabled: Boolean = false,
    val sendResearch: Boolean = true,
    val sendChatResults: Boolean = false,
)

@Serializable
data class Profile(
    val id: String = "",
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class MarketSymbol(
    val symbol: String = "",
    val name: String = "",
    val market: String = "crypto",
)

@Serializable
data class MarketData(
    val price: Double? = null,
    val change: Double? = null,
    val changePercent: Double? = null,
    val high: Double? = null,
    val low: Double? = null,
    val volume: Double? = null,
    val signal: String? = null,
    val score: Double? = null,
)

@Serializable
data class AgentRequest(
    val goal: String,
    val sessionId: String? = null,
    val market: String? = null,
    val symbol: String? = null,
    val timeframe: String? = null,
    val feature: String? = null,
    val query: String? = null,
)

@Serializable
data class AgentResponse(
    val sessionId: String? = null,
    val content: String? = null,
    val summary: String? = null,
    val provider: String? = null,
    val market: String? = null,
    val symbol: String? = null,
    val timeframe: String? = null,
    val toolsUsed: List<String> = emptyList(),
    val toolCalls: List<String> = emptyList(),
    val tasks: List<String> = emptyList(),
    val marketData: MarketData? = null,
    val timestamp: String? = null,
)

data class PaperOrderRequest(
    val symbol: String,
    val side: String,
    val quantity: Double,
    val orderType: String = "market",
    val limitPrice: Double? = null,
)

@Serializable
data class PaperDashboardData(
    val account: PaperAccount?,
    val positions: List<PaperPosition>,
    val orders: List<PaperOrder>,
    val trades: List<PaperTrade>,
)

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class PaperAccount(
    val id: String = "",
    val userId: String = "",
    val balance: Double = 0.0,
    val equity: Double = 0.0,
    val buyingPower: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class PaperPosition(
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

@Serializable
data class PaperOrder(
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

@Serializable
data class PaperTrade(
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
