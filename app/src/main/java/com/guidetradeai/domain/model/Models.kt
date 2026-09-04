package com.guidetradeai.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val email: String = "",
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)

data class ChatSession(
    val id: String,
    val userId: String,
    val title: String,
    val createdAt: String,
    val updatedAt: String,
)

data class ChatMessage(
    val id: String,
    val sessionId: String,
    val userId: String,
    val role: String,
    val content: String,
    val createdAt: String,
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
