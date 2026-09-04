package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AiChatResponse
import com.guidetradeai.domain.model.ResearchResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
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
                "message": ${Json.encodeToString(message)}
            }
            """.trimIndent()
            val response = supabase.functions.invoke("ai-chat", body = body)
            val data = response.data
            val jsonObject = json.decodeFromString<JsonObject>(data)
            val error = jsonObject.jsonObject["error"]?.jsonPrimitive?.contentOrNull
            if (error != null) {
                return Result.error(mapFunctionError(error))
            }
            val content = jsonObject.jsonObject["content"]?.jsonPrimitive?.contentOrNull ?: ""
            val role = jsonObject.jsonObject["role"]?.jsonPrimitive?.contentOrNull ?: "assistant"
            val timestamp = jsonObject.jsonObject["timestamp"]?.jsonPrimitive?.contentOrNull
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
            val result = supabase.postgrest.from("research_results").insert(
                mapOf(
                    "user_id" to userId,
                    "session_id" to sessionId,
                    "title" to title,
                    "query" to query,
                    "asset" to asset,
                    "response" to response,
                ),
            ) {
                select()
            }
            val row = result.firstOrNull()
            if (row != null) {
                Result.success(mapToResearchResult(row.jsonObject))
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
                eq("user_id", currentUserId())
                order("created_at", ascending = false)
            }
            Result.success(result.map { mapToResearchResult(it.jsonObject) })
        } catch (e: Exception) {
            Result.error("Failed to load research history: ${e.message}")
        }
    }

    suspend fun getResearchResult(id: String): Result<ResearchResult> {
        return try {
            val result = supabase.postgrest.from("research_results").select {
                eq("id", id)
            }
            val row = result.firstOrNull()
            if (row != null) {
                Result.success(mapToResearchResult(row.jsonObject))
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
                    eq("id", id)
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
            val assetJson = if (asset != null) Json.encodeToString(asset) else "null"
            val researchIdJson = if (researchId != null) Json.encodeToString(researchId) else "null"
            val body = """
            {
                "research_id": $researchIdJson,
                "title": ${Json.encodeToString(title)},
                "query": ${Json.encodeToString(query)},
                "response": ${Json.encodeToString(response)},
                "asset": $assetJson
            }
            """.trimIndent()
            val resp = supabase.functions.invoke("telegram-send", body = body)
            val data = resp.data
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
            Result.error("Failed to send to Telegram: ${e.message}")
        }
    }

    private fun mapToResearchResult(obj: JsonObject): ResearchResult {
        return ResearchResult(
            id = obj["id"]?.jsonPrimitive?.content ?: "",
            userId = obj["user_id"]?.jsonPrimitive?.content ?: "",
            sessionId = obj["session_id"]?.jsonPrimitive?.contentOrNull,
            title = obj["title"]?.jsonPrimitive?.content ?: "",
            query = obj["query"]?.jsonPrimitive?.content ?: "",
            asset = obj["asset"]?.jsonPrimitive?.contentOrNull,
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
