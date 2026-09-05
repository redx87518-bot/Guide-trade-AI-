package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.bodyAsText
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChatRepository(private val supabase: SupabaseClient) {

    suspend fun createSession(userId: String, title: String = "New Chat"): Result<String> {
        return try {
            val sessionId = java.util.UUID.randomUUID().toString()
            supabase.postgrest.from("chat_sessions")
                .insert(buildJsonObject {
                    put("id", JsonPrimitive(sessionId))
                    put("user_id", JsonPrimitive(userId))
                    put("title", JsonPrimitive(title))
                })
            Result.success(sessionId)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to create session")
        }
    }

    suspend fun renameSession(sessionId: String, title: String): Result<Unit> {
        return try {
            supabase.postgrest.from("chat_sessions")
                .update(buildJsonObject { put("title", JsonPrimitive(title)) }) {
                    filter { eq("id", sessionId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to rename session")
        }
    }

    suspend fun getSessions(userId: String): Result<List<ChatSession>> {
        return try {
            val result = supabase.postgrest.from("chat_sessions")
                .select { filter { eq("user_id", userId) } }
            val rows = result.decodeList<JsonObject>()
            val sessions = rows.map { row ->
                ChatSession(
                    id = row["id"]?.jsonPrimitive?.content ?: "",
                    userId = row["user_id"]?.jsonPrimitive?.content ?: "",
                    title = row["title"]?.jsonPrimitive?.content ?: "",
                    createdAt = row["created_at"]?.jsonPrimitive?.content ?: "",
                    updatedAt = row["updated_at"]?.jsonPrimitive?.content ?: "",
                )
            }.sortedByDescending { it.createdAt }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to load sessions")
        }
    }

    suspend fun getMessages(sessionId: String): Result<List<ChatMessage>> {
        return try {
            val result = supabase.postgrest.from("chat_messages")
                .select { filter { eq("session_id", sessionId) } }
            val rows = result.decodeList<JsonObject>()
            val messages = rows.map { row ->
                ChatMessage(
                    id = row["id"]?.jsonPrimitive?.content ?: "",
                    sessionId = row["session_id"]?.jsonPrimitive?.content ?: "",
                    userId = row["user_id"]?.jsonPrimitive?.content ?: "",
                    role = row["role"]?.jsonPrimitive?.content ?: "",
                    content = row["content"]?.jsonPrimitive?.content ?: "",
                    createdAt = row["created_at"]?.jsonPrimitive?.content ?: "",
                )
            }.sortedBy { it.createdAt }
            Result.success(messages)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to load messages")
        }
    }

    suspend fun sendMessage(sessionId: String, message: String): Result<String> {
        return try {
            val response = supabase.functions.invoke(
                "ai-chat",
                buildJsonObject {
                    put("session_id", JsonPrimitive(sessionId))
                    put("message", JsonPrimitive(message))
                }
            )
            val data = response.bodyAsText()
            val parsed = Json.parseToJsonElement(data).jsonObject
            val content = parsed["content"]?.jsonPrimitive?.content
                ?: return Result.error(parsed["error"]?.jsonPrimitive?.content ?: "Empty response")
            Result.success(content)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to send message")
        }
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            supabase.postgrest.from("chat_messages").delete { filter { eq("session_id", sessionId) } }
            supabase.postgrest.from("chat_sessions").delete { filter { eq("id", sessionId) } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to delete session")
        }
    }
}
