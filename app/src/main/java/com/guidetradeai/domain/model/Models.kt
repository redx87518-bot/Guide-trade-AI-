package com.guidetradeai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
data class ChatSession(
    val id: String,
    val userId: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class ChatMessage(
    val id: String,
    val sessionId: String,
    val userId: String,
    val role: String,
    val content: String,
    val createdAt: String,
    val marketData: MarketDataResponse? = null,
)

@Serializable
data class AiChatResponse(
    val role: String? = null,
    val content: String? = null,
    val timestamp: String? = null,
)

data class ResearchResult(
    val id: String = "",
    val userId: String = "",
    val sessionId: String? = null,
    val title: String = "",
    val query: String = "",
    val asset: String?,
    val response: String,
    val createdAt: String = "",
)

@Serializable
data class UserSettings(
    val userId: String = "",
    @SerialName("voice_enabled") val voiceEnabled: Boolean = true,
    @SerialName("auto_speak") val autoSpeak: Boolean = false,
    val theme: String = "dark",
    val createdAt: String = "",
    val updatedAt: String = "",
)

@Serializable
data class TelegramSettings(
    @SerialName("bot_token_encrypted") val botTokenEncrypted: String? = null,
    @SerialName("chat_id") val chatId: String? = null,
    val enabled: Boolean = false,
    @SerialName("send_research") val sendResearch: Boolean = true,
    @SerialName("send_chat_results") val sendChatResults: Boolean = false,
    val userId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
)

data class MarketIntelligenceRequest(
    val provider: String,
    val feature: String,
    val market: String? = null,
    val symbol: String? = null,
    val timeframe: String? = null,
    val query: String? = null,
)

data class MarketIntelligenceResponse(
    val provider: String,
    val feature: String,
    val market: String? = null,
    val symbol: String? = null,
    val timeframe: String? = null,
    val result: JsonObject? = null,
)

@Serializable
data class MarketDataResponse(
    val provider: String = "",
    val market: String = "",
    val symbol: String = "",
    val name: String = "",
    val timestamp: String = "",
    val price: Double? = null,
    val change: Double? = null,
    val changePercent: Double? = null,
    val high: Double? = null,
    val low: Double? = null,
    val signal: String = "",
    val score: Double? = null,
    val oscillator: String = "",
    val movingAverage: String = "",
    val rsi: Double? = null,
    val macd: String = "",
    val stochastic: String = "",
    val cci: Double? = null,
    val williamsR: Double? = null,
    val momentum: String = "",
    val sma: Double? = null,
    val ema: Double? = null,
    val barStatus: String = "",
    val sentiment: String = "",
    val trend: String = "",
    val news: List<NewsItem> = emptyList(),
    val chartData: List<ChartPoint> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
)

data class NewsItem(
    val title: String,
    val summary: String = "",
    val timestamp: String = "",
    val source: String = "",
)

data class ChartPoint(
    val timestamp: String,
    val value: Double,
    val volume: Double? = null,
)

data class SymbolItem(
    val symbol: String,
    val name: String = "",
    val market: String = "",
)
