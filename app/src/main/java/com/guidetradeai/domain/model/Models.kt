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
