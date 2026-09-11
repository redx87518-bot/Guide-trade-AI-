package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.UserSettings

class SettingsRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun getUserSettings(): Result<UserSettings> {
        return try {
            Result.Success(UserSettings())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load settings")
        }
    }

    suspend fun updateVoiceEnabled(enabled: Boolean): Result<Unit> {
        return try {
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update settings")
        }
    }

    suspend fun updateAutoSpeak(enabled: Boolean): Result<Unit> {
        return try {
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update settings")
        }
    }

    suspend fun updateTheme(theme: String): Result<Unit> {
        return try {
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update theme")
        }
    }
}
