package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsData(
    val user_id: String = "",
    val voice_enabled: Boolean = true,
    val auto_speak: Boolean = false,
    val theme: String = "dark",
    val created_at: String = "",
    val updated_at: String = "",
)

@Serializable
data class ChatSessionData(
    val id: String = "",
    val user_id: String = "",
    val title: String = "New Chat",
    val created_at: String = "",
    val updated_at: String = "",
)

@Serializable
data class ChatMessageData(
    val id: String = "",
    val session_id: String = "",
    val user_id: String = "",
    val role: String = "user",
    val content: String = "",
    val created_at: String = "",
)

@Serializable
data class ResearchResultData(
    val id: String = "",
    val user_id: String = "",
    val session_id: String? = null,
    val title: String = "",
    val query: String = "",
    val asset: String? = null,
    val response: String = "",
    val created_at: String = "",
)

@Serializable
data class TelegramSettingsData(
    val user_id: String = "",
    val bot_token_encrypted: String? = null,
    val chat_id: String? = null,
    val enabled: Boolean = false,
    val send_research: Boolean = true,
    val send_chat_results: Boolean = false,
    val created_at: String = "",
    val updated_at: String = "",
)

@Serializable
data class ProfileData(
    val id: String = "",
    val full_name: String? = null,
    val avatar_url: String? = null,
    val created_at: String = "",
    val updated_at: String = "",
)
