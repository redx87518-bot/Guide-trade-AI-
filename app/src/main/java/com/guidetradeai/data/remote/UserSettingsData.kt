package com.guidetradeai.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsData(
    val userId: String = "",
    val voiceEnabled: Boolean = true,
    val autoSpeak: Boolean = false,
    val theme: String = "dark",
    val createdAt: String = "",
    val updatedAt: String = "",
)
