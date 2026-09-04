package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.UserSettings
import io.github.supabase.SupabaseClient
import io.github.supabase.auth.auth
import io.github.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SettingsRepository(
    private val supabase: SupabaseClient,
) {
    private fun currentUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: ""
    }

    suspend fun getUserSettings(): Result<UserSettings> {
        return try {
            val result = supabase.postgrest.from("user_settings").select {
                eq("user_id", currentUserId())
            }
            val row = result.firstOrNull()
            if (row != null) {
                Result.success(mapToSettings(row.jsonObject))
            } else {
                Result.success(UserSettings(userId = currentUserId()))
            }
        } catch (e: Exception) {
            Result.error("Failed to load settings: ${e.message}")
        }
    }

    suspend fun updateVoiceEnabled(enabled: Boolean): Result<Unit> {
        return try {
            supabase.postgrest.from("user_settings")
                .update(mapOf("voice_enabled" to enabled)) {
                    eq("user_id", currentUserId())
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to update settings: ${e.message}")
        }
    }

    suspend fun updateAutoSpeak(enabled: Boolean): Result<Unit> {
        return try {
            supabase.postgrest.from("user_settings")
                .update(mapOf("auto_speak" to enabled)) {
                    eq("user_id", currentUserId())
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to update settings: ${e.message}")
        }
    }

    suspend fun updateTheme(theme: String): Result<Unit> {
        return try {
            supabase.postgrest.from("user_settings")
                .update(mapOf("theme" to theme)) {
                    eq("user_id", currentUserId())
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to update theme: ${e.message}")
        }
    }

    private fun mapToSettings(obj: JsonObject): UserSettings {
        return UserSettings(
            userId = obj["user_id"]?.jsonPrimitive?.content ?: "",
            voiceEnabled = obj["voice_enabled"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true,
            autoSpeak = obj["auto_speak"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
            theme = obj["theme"]?.jsonPrimitive?.content ?: "dark",
            createdAt = obj["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = obj["updated_at"]?.jsonPrimitive?.content ?: "",
        )
    }
}
