package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChatRepository(
    private val supabase: SupabaseClient,
) {
    private fun currentUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: ""
    }

    suspend fun createChatSession(title: String): Result<ChatSession> {
        return try {
            val result = supabase.postgrest.from("chat_sessions").insert(
                buildJsonObject {
                    put("title", title)
                    put("user_id", currentUserId())
                },
            ) {
                select()
            }
            val row = result.decodeSingleOrNull<JsonObject>()
            if (row != null) {
                Result.success(mapToChatSession(row))
            } else {
                Result.error("Failed to create chat session")
            }
        } catch (e: Exception) {
            Result.error("Failed to create chat session: ${e.message}")
        }
    }

    suspend fun getChatSessions(): Result<List<ChatSession>> {
        return try {
            val result = supabase.postgrest.from("chat_sessions").select {
                filter { eq("user_id", currentUserId()) }
                order("updated_at", Order.DESCENDING)
            }
            val rows = result.decodeList<JsonObject>()
            Result.success(rows.map { mapToChatSession(it) })
        } catch (e: Exception) {
            Result.error("Failed to load chat sessions: ${e.message}")
        }
    }

    suspend fun getChatMessages(sessionId: String): Result<List<ChatMessage>> {
        return try {
            val result = supabase.postgrest.from("chat_messages").select {
                filter { eq("session_id", sessionId) }
                order("created_at", Order.ASCENDING)
            }
            val rows = result.decodeList<JsonObject>()
            Result.success(rows.map { mapToChatMessage(it) })
        } catch (e: Exception) {
            Result.error("Failed to load messages: ${e.message}")
        }
    }

    suspend fun saveMessage(
        sessionId: String,
        role: String,
        content: String,
    ): Result<ChatMessage> {
        return try {
            val result = supabase.postgrest.from("chat_messages").insert(
                buildJsonObject {
                    put("session_id", sessionId)
                    put("user_id", currentUserId())
                    put("role", role)
                    put("content", content)
                },
            ) {
                select()
            }
            val row = result.decodeSingleOrNull<JsonObject>()
            if (row != null) {
                Result.success(mapToChatMessage(row))
            } else {
                Result.error("Failed to save message")
            }
        } catch (e: Exception) {
            Result.error("Failed to save message: ${e.message}")
        }
    }

    suspend fun renameSession(sessionId: String, newTitle: String): Result<Unit> {
        return try {
            supabase.postgrest.from("chat_sessions")
                .update(
                    buildJsonObject { put("title", newTitle) },
                ) {
                    filter { eq("id", sessionId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to rename session: ${e.message}")
        }
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            supabase.postgrest.from("chat_sessions")
                .delete {
                    filter { eq("id", sessionId) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to delete session: ${e.message}")
        }
    }

    private fun mapToChatSession(row: JsonObject): ChatSession {
        return ChatSession(
            id = row["id"]?.jsonPrimitive?.content ?: "",
            userId = row["user_id"]?.jsonPrimitive?.content ?: "",
            title = row["title"]?.jsonPrimitive?.content ?: "",
            createdAt = row["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = row["updated_at"]?.jsonPrimitive?.content ?: "",
        )
    }

    private fun mapToChatMessage(row: JsonObject): ChatMessage {
        return ChatMessage(
            id = row["id"]?.jsonPrimitive?.content ?: "",
            sessionId = row["session_id"]?.jsonPrimitive?.content ?: "",
            userId = row["user_id"]?.jsonPrimitive?.content ?: "",
            role = row["role"]?.jsonPrimitive?.content ?: "",
            content = row["content"]?.jsonPrimitive?.content ?: "",
            createdAt = row["created_at"]?.jsonPrimitive?.content ?: "",
        )
    }
}
