package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.data.remote.UserSettingsData
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingsRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getUserSettings(): Result<UserSettings> {
        return try {
            val data = supabase.getUserSettings()
            if (data is Result.Success) {
                Result.Success(UserSettings(
                    voiceEnabled = data.data.voice_enabled,
                    autoSpeak = data.data.auto_speak,
                    theme = data.data.theme,
                ))
            } else {
                Result.Error("Settings not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load settings")
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
