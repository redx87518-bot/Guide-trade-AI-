package com.guidetradeai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

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

@Serializable
data class NewsItem(
    val title: String = "",
    val summary: String = "",
    val timestamp: String = "",
    val source: String = "",
)

@Serializable
data class ChartPoint(
    val timestamp: String = "",
    val value: Double = 0.0,
    val volume: Double? = null,
)

data class SymbolItem(
    val symbol: String,
    val name: String = "",
    val market: String = "",
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
    val toolCalls: List<AgentToolCall> = emptyList(),
    val tasks: List<AgentTask> = emptyList(),
    val marketData: MarketDataResponse? = null,
    val timestamp: String? = null,
    val rawPayload: JsonObject? = null,
)

@Serializable
data class AgentToolCall(
    val name: String,
    val status: String,
    val startedAt: String? = null,
    val finishedAt: String? = null,
)

@Serializable
data class AgentTask(
    val id: String,
    val description: String,
    val status: String,
    val createdAt: String? = null,
)

@Serializable
data class AgentSession(
    val id: String,
    val title: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class McpConnection(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val provider: String = "",
    val type: String = "",
    val status: String = "disconnected",
    val permissions: String = "read_only",
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

data class PaperOrderRequest(
    val symbol: String,
    val side: String,
    val quantity: Double,
    val orderType: String = "market",
    val limitPrice: Double? = null,
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

data class PaperDashboardData(
    val account: PaperAccount?,
    val positions: List<PaperPosition>,
    val orders: List<PaperOrder>,
    val trades: List<PaperTrade>,
)