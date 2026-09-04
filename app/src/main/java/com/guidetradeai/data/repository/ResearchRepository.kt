package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AiChatResponse
import com.guidetradeai.domain.model.ResearchResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ResearchRepository(
    private val supabase: SupabaseClient,
) {
    private fun currentUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: ""
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun sendAiMessage(sessionId: String, message: String): Result<AiChatResponse> {
        return try {
            val body = """
            {
                "session_id": "$sessionId",
                "message": ${json.encodeToString(JsonPrimitive(message))}
            }
            """.trimIndent()
            val response = supabase.functions.invoke("ai-chat", body = body)
            val data = response.bodyAsText()
            val jsonObject = json.decodeFromString<JsonObject>(data)
            val error = jsonObject.jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(mapFunctionError(error))
            }
            val content = jsonObject.jsonObject["content"]?.jsonPrimitive?.content ?: ""
            val role = jsonObject.jsonObject["role"]?.jsonPrimitive?.content ?: "assistant"
            val timestamp = jsonObject.jsonObject["timestamp"]?.jsonPrimitive?.content
            Result.success(AiChatResponse(role = role, content = content, timestamp = timestamp))
        } catch (e: Exception) {
            Result.error("Failed to get AI response: ${e.message}")
        }
    }

    suspend fun saveResearchResult(
        userId: String,
        sessionId: String,
        title: String,
        query: String,
        asset: String?,
        response: String,
    ): Result<ResearchResult> {
        return try {
            val data = buildJsonObject {
                put("user_id", userId)
                put("session_id", sessionId)
                put("title", title)
                put("query", query)
                put("asset", asset ?: "")
                put("response", response)
            }
            val result = supabase.postgrest.from("research_results").insert(data) {
                select()
            }
            val row = result.decodeSingleOrNull<JsonObject>()
            if (row != null) {
                Result.success(mapToResearchResult(row))
            } else {
                Result.error("Failed to save research result")
            }
        } catch (e: Exception) {
            Result.error("Failed to save research result: ${e.message}")
        }
    }

    suspend fun getResearchHistory(): Result<List<ResearchResult>> {
        return try {
            val result = supabase.postgrest.from("research_results").select {
                filter { eq("user_id", currentUserId()) }
                order("created_at", Order.DESCENDING)
            }
            val rows = result.decodeList<JsonObject>()
            Result.success(rows.map { mapToResearchResult(it) })
        } catch (e: Exception) {
            Result.error("Failed to load research history: ${e.message}")
        }
    }

    suspend fun getResearchResult(id: String): Result<ResearchResult> {
        return try {
            val result = supabase.postgrest.from("research_results").select {
                filter { eq("id", id) }
            }
            val row = result.decodeSingleOrNull<JsonObject>()
            if (row != null) {
                Result.success(mapToResearchResult(row))
            } else {
                Result.error("Research result not found")
            }
        } catch (e: Exception) {
            Result.error("Failed to load research result: ${e.message}")
        }
    }

    suspend fun deleteResearchResult(id: String): Result<Unit> {
        return try {
            supabase.postgrest.from("research_results")
                .delete {
                    filter { eq("id", id) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to delete research result: ${e.message}")
        }
    }

    suspend fun sendToTelegram(
        title: String,
        query: String,
        response: String,
        asset: String?,
        researchId: String?,
    ): Result<String> {
        return try {
            val assetJson = if (asset != null) json.encodeToString(JsonPrimitive(asset)) else "null"
            val researchIdJson = if (researchId != null) json.encodeToString(JsonPrimitive(researchId)) else "null"
            val body = """
            {
                "research_id": $researchIdJson,
                "title": ${json.encodeToString(JsonPrimitive(title))},
                "query": ${json.encodeToString(JsonPrimitive(query))},
                "response": ${json.encodeToString(JsonPrimitive(response))},
                "asset": $assetJson
            }
            """.trimIndent()
            val resp = supabase.functions.invoke("telegram-send", body = body)
            val data = resp.bodyAsText()
            val jsonObj = json.decodeFromString<JsonObject>(data)
            val success = jsonObj.jsonObject["success"]?.jsonPrimitive?.content?.toBooleanStrictOrNull()
            val message = jsonObj.jsonObject["message"]?.jsonPrimitive?.content ?: ""
            val error = jsonObj.jsonObject["error"]?.jsonPrimitive?.content
            if (error != null && success != true) {
                Result.error(error)
            } else {
                Result.success(message)
            }
        } catch (e: Exception) {
            Result.error("Failed to send to Telegram: ${e.message}")
        }
    }

    private fun mapToResearchResult(obj: JsonObject): ResearchResult {
        return ResearchResult(
            id = obj["id"]?.jsonPrimitive?.content ?: "",
            userId = obj["user_id"]?.jsonPrimitive?.content ?: "",
            sessionId = obj["session_id"]?.jsonPrimitive?.content,
            title = obj["title"]?.jsonPrimitive?.content ?: "",
            query = obj["query"]?.jsonPrimitive?.content ?: "",
            asset = obj["asset"]?.jsonPrimitive?.content,
            response = obj["response"]?.jsonPrimitive?.content ?: "",
            createdAt = obj["created_at"]?.jsonPrimitive?.content ?: "",
        )
    }

    private fun mapFunctionError(error: String): String {
        return when (error) {
            "AUTH_REQUIRED" -> "Authentication required."
            "INVALID_REQUEST" -> "Invalid request."
            "QUAN_ERROR" -> "AI service error."
            "QUAN_TIMEOUT" -> "AI request timed out."
            "RATE_LIMITED" -> "Rate limit exceeded."
            "DATABASE_ERROR" -> "Database error."
            "VOICE_DISABLED" -> "Voice is disabled."
            "TELEGRAM_NOT_CONFIGURED" -> "Telegram is not configured."
            "TELEGRAM_DISABLED" -> "Telegram notifications are disabled."
            "UNKNOWN_ERROR" -> "An unknown error occurred."
            else -> error
        }
    }
}
