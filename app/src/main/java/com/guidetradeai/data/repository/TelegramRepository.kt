package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.TelegramSettings
import io.github.supabase.SupabaseClient
import io.github.supabase.auth.auth
import io.github.supabase.functions.functions
import io.github.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TelegramRepository(
    private val supabase: SupabaseClient,
) {
    private fun currentUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: ""
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getTelegramSettings(): Result<TelegramSettings> {
        return try {
            val result = supabase.postgrest.from("telegram_settings").select {
                eq("user_id", currentUserId())
            }
            val row = result.firstOrNull()
            if (row != null) {
                Result.success(mapToTelegramSettings(row.jsonObject))
            } else {
                Result.success(TelegramSettings(userId = currentUserId()))
            }
        } catch (e: Exception) {
            Result.error("Failed to load Telegram settings: ${e.message}")
        }
    }

    suspend fun testAndSaveConnection(
        botToken: String,
        chatId: String,
        sendResearch: Boolean,
        sendChatResults: Boolean,
    ): Result<String> {
        return try {
            val body = """
            {
                "action": "test",
                "bot_token": ${Json.encodeToString(botToken)},
                "chat_id": ${Json.encodeToString(chatId)}
            }
            """.trimIndent()
            val resp = supabase.functions.invoke("telegram-test", body = body)
            parseSuccessResponse(resp.data)
        } catch (e: Exception) {
            Result.error("Failed to test Telegram connection: ${e.message}")
        }
    }

    suspend fun saveSettings(
        botToken: String,
        chatId: String,
        enabled: Boolean,
        sendResearch: Boolean,
        sendChatResults: Boolean,
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "bot_token" to botToken,
                "chat_id" to chatId,
                "enabled" to enabled,
                "send_research" to sendResearch,
                "send_chat_results" to sendChatResults,
            )
            supabase.postgrest.from("telegram_settings")
                .upsert(updates) {
                    eq("user_id", currentUserId())
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to save Telegram settings: ${e.message}")
        }
    }

    suspend fun disableTelegram(): Result<Unit> {
        return try {
            supabase.postgrest.from("telegram_settings")
                .update(mapOf("enabled" to false)) {
                    eq("user_id", currentUserId())
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to disable Telegram: ${e.message}")
        }
    }

    fun maskToken(encryptedToken: String?): String {
        if (encryptedToken.isNullOrEmpty()) return ""
        val last4 = if (encryptedToken.length >= 4) encryptedToken.takeLast(4) else "••••"
        return "••••••••••$last4"
    }

    suspend fun sendResearchToTelegram(
        title: String,
        query: String,
        response: String,
        asset: String?,
        researchId: String?,
    ): Result<String> {
        return try {
            val body = buildTelegramSendBody(title, query, response, asset, researchId)
            val resp = supabase.functions.invoke("telegram-send", body = body)
            parseSuccessResponse(resp.data)
        } catch (e: Exception) {
            Result.error("Failed to send to Telegram: ${e.message}")
        }
    }

    private fun parseSuccessResponse(data: String): Result<String> {
        return try {
            val jsonObj = json.decodeFromString<JsonObject>(data)
            val success = jsonObj.jsonObject["success"]?.jsonPrimitive?.content?.toBooleanStrictOrNull()
            val message = jsonObj.jsonObject["message"]?.jsonPrimitive?.content ?: ""
            val error = jsonObj.jsonObject["error"]?.jsonPrimitive?.contentOrNull
            if (error != null && success != true) {
                Result.error(error)
            } else {
                Result.success(message)
            }
        } catch (e: Exception) {
            Result.success(data)
        }
    }

    private fun escapeJson(text: String): String {
        return text.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun buildTelegramSendBody(
        title: String,
        query: String,
        response: String,
        asset: String?,
        researchId: String?,
    ): String {
        val assetJson = if (asset != null) "\"${escapeJson(asset)}\"" else "null"
        val researchIdJson = if (researchId != null) "\"${escapeJson(researchId)}\"" else "null"
        return """
        {
            "research_id": $researchIdJson,
            "title": "${escapeJson(title)}",
            "query": "${escapeJson(query)}",
            "response": "${escapeJson(response)}",
            "asset": $assetJson
        }
        """.trimIndent()
    }

    private fun mapToTelegramSettings(obj: JsonObject): TelegramSettings {
        return TelegramSettings(
            userId = obj["user_id"]?.jsonPrimitive?.content ?: "",
            botTokenEncrypted = obj["bot_token_encrypted"]?.jsonPrimitive?.contentOrNull,
            chatId = obj["chat_id"]?.jsonPrimitive?.contentOrNull,
            enabled = obj["enabled"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
            sendResearch = obj["send_research"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true,
            sendChatResults = obj["send_chat_results"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
            createdAt = obj["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = obj["updated_at"]?.jsonPrimitive?.content ?: "",
        )
    }
}
