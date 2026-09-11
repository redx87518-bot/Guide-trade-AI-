package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageData(
    val id: String = "",
    val sessionId: String = "",
    val userId: String = "",
    val role: String = "user",
    val content: String = "",
    val createdAt: String = "",
)
