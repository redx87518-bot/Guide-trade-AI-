package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.UserSettings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
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
                filter { eq("user_id", currentUserId()) }
                order("created_at", Order.DESCENDING)
            }
            val rows = result.decodeList<JsonObject>()
            val row = rows.firstOrNull()
            if (row != null) {
                Result.success(mapToSettings(row))
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
                .update(
                    buildJsonObject { put("voice_enabled", JsonPrimitive(enabled)) },
                ) {
                    filter { eq("user_id", currentUserId()) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to update settings: ${e.message}")
        }
    }

    suspend fun updateAutoSpeak(enabled: Boolean): Result<Unit> {
        return try {
            supabase.postgrest.from("user_settings")
                .update(
                    buildJsonObject { put("auto_speak", JsonPrimitive(enabled)) },
                ) {
                    filter { eq("user_id", currentUserId()) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to update settings: ${e.message}")
        }
    }

    suspend fun updateTheme(theme: String): Result<Unit> {
        return try {
            supabase.postgrest.from("user_settings")
                .update(
                    buildJsonObject { put("theme", JsonPrimitive(theme)) },
                ) {
                    filter { eq("user_id", currentUserId()) }
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
