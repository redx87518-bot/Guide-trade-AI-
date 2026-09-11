package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getUserSettings(): Result<UserSettings> {
        return supabase.getUserSettings().map { data ->
            UserSettings(
                voiceEnabled = data.voice_enabled,
                autoSpeak = data.auto_speak,
                theme = data.theme,
            )
        }
    }

    suspend fun updateVoiceEnabled(enabled: Boolean): Result<Unit> {
        val current = getUserSettings().getOrNull() ?: return Result.Error("Settings not found")
        return supabase.updateUserSettings(
            UserSettingsData(
                user_id = supabase.currentUserId(),
                voice_enabled = enabled,
                auto_speak = current.autoSpeak,
                theme = current.theme,
            )
        )
    }

    suspend fun updateAutoSpeak(enabled: Boolean): Result<Unit> {
        val current = getUserSettings().getOrNull() ?: return Result.Error("Settings not found")
        return supabase.updateUserSettings(
            UserSettingsData(
                user_id = supabase.currentUserId(),
                voice_enabled = current.voiceEnabled,
                auto_speak = enabled,
                theme = current.theme,
            )
        )
    }

    suspend fun updateTheme(theme: String): Result<Unit> {
        val current = getUserSettings().getOrNull() ?: return Result.Error("Settings not found")
        return supabase.updateUserSettings(
            UserSettingsData(
                user_id = supabase.currentUserId(),
                voice_enabled = current.voiceEnabled,
                auto_speak = current.autoSpeak,
                theme = theme,
            )
        )
    }
}
