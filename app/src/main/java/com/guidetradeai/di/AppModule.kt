package com.guidetradeai.di

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository

object AppModule {
    val supabaseClient by lazy { SupabaseClient.client }
    val authRepository: AuthRepository by lazy { AuthRepository(supabaseClient) }
    val chatRepository: ChatRepository by lazy { ChatRepository(supabaseClient) }
    val researchRepository: ResearchRepository by lazy { ResearchRepository(supabaseClient) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(supabaseClient) }
    val telegramRepository: TelegramRepository by lazy { TelegramRepository(supabaseClient) }
}
