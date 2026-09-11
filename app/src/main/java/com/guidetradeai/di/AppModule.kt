package com.guidetradeai.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.audio.VoiceManager
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.data.repository.PaperTradingRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

val Context.dataStore by preferencesDataStore("app_prefs")

object AppModule {
    val supabaseClient by lazy { SupabaseClient.client }
    val authRepository: AuthRepository by lazy { AuthRepository(supabaseClient) }
    val chatRepository: ChatRepository by lazy { ChatRepository(supabaseClient) }
    val researchRepository: ResearchRepository by lazy { ResearchRepository(supabaseClient) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepository(supabaseClient) }
    val telegramRepository: TelegramRepository by lazy { TelegramRepository(supabaseClient) }
    val guideTradeAgentRepository: GuideTradeAgentRepository by lazy { GuideTradeAgentRepository(supabaseClient) }
    val paperTradingRepository: PaperTradingRepository by lazy { PaperTradingRepository(supabaseClient) }
    lateinit var applicationContext: Context
    val voiceManager: VoiceManager by lazy {
        VoiceManager(context = applicationContext, supabase = supabaseClient)
    }
    lateinit var appPreferences: AppPreferences
}