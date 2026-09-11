package com.guidetradeai.di

import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.data.repository.AgentRepository
import com.guidetradeai.data.repository.PaperTradingRepository
import com.guidetradeai.data.repository.McpRepository

object AppModule {
    val authRepository by lazy { AuthRepository() }
    val chatRepository by lazy { ChatRepository() }
    val settingsRepository by lazy { SettingsRepository() }
    val telegramRepository by lazy { TelegramRepository() }
    val agentRepository by lazy { AgentRepository() }
    val paperTradingRepository by lazy { PaperTradingRepository() }
    val mcpRepository by lazy { McpRepository() }
}
