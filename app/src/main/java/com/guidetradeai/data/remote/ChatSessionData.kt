package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ChatSessionData(
    val id: String = "",
    val userId: String = "",
    val title: String = "New Chat",
    val createdAt: String = "",
    val updatedAt: String = "",
)
