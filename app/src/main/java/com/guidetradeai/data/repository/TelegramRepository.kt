package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.data.remote.TelegramSettingsData
import com.guidetradeai.domain.Result

class TelegramRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getSettings(): Result<TelegramSettingsData?> {
        return supabase.getTelegramSettings()
    }

    suspend fun saveSettings(settings: TelegramSettingsData): Result<Unit> {
        return supabase.saveTelegramSettings(settings)
    }
}
