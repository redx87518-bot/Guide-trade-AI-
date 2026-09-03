package com.guidetradeai.di

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository

object AppModule {
    val supabaseClient = SupabaseClient.client

    val authRepository: AuthRepository by lazy { AuthRepository(supabaseClient) }
    val chatRepository: ChatRepository by lazy { ChatRepository(supabaseClient) }
    val researchRepository: ResearchRepository by lazy { ResearchRepository(supabaseClient) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(supabaseClient) }
    val telegramRepository: TelegramRepository by lazy { TelegramRepository(supabaseClient) }
}

class ViewModelFactory<T : androidx.lifecycle.ViewModel>(
    private val creator: () -> T,
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}
