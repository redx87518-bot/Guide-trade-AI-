package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import io.github.supabase.SupabaseClient
import io.github.supabase.auth.auth
import io.github.supabase.postgrest.postgrest
import kotlinx.serialization.json.JsonObject
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
                mapOf("title" to title, "user_id" to currentUserId()),
            ) {
                select()
            }
            val row = result.firstOrNull()
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
                eq("user_id", currentUserId())
                order("updated_at", ascending = false)
            }
            Result.success(result.map { mapToChatSession(it) })
        } catch (e: Exception) {
            Result.error("Failed to load chat sessions: ${e.message}")
        }
    }

    suspend fun getChatMessages(sessionId: String): Result<List<ChatMessage>> {
        return try {
            val result = supabase.postgrest.from("chat_messages").select {
                eq("session_id", sessionId)
                order("created_at", ascending = true)
            }
            Result.success(result.map { mapToChatMessage(it) })
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
                mapOf(
                    "session_id" to sessionId,
                    "user_id" to currentUserId(),
                    "role" to role,
                    "content" to content,
                ),
            ) {
                select()
            }
            val row = result.firstOrNull()
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
                .update(mapOf("title" to newTitle)) {
                    eq("id", sessionId)
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
                    eq("id", sessionId)
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error("Failed to delete session: ${e.message}")
        }
    }

    private fun mapToChatSession(row: JsonObject): ChatSession {
        return ChatSession(
            id = row.jsonObject["id"]?.jsonPrimitive?.content ?: "",
            userId = row.jsonObject["user_id"]?.jsonPrimitive?.content ?: "",
            title = row.jsonObject["title"]?.jsonPrimitive?.content ?: "",
            createdAt = row.jsonObject["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = row.jsonObject["updated_at"]?.jsonPrimitive?.content ?: "",
        )
    }

    private fun mapToChatMessage(row: JsonObject): ChatMessage {
        return ChatMessage(
            id = row.jsonObject["id"]?.jsonPrimitive?.content ?: "",
            sessionId = row.jsonObject["session_id"]?.jsonPrimitive?.content ?: "",
            userId = row.jsonObject["user_id"]?.jsonPrimitive?.content ?: "",
            role = row.jsonObject["role"]?.jsonPrimitive?.content ?: "",
            content = row.jsonObject["content"]?.jsonPrimitive?.content ?: "",
            createdAt = row.jsonObject["created_at"]?.jsonPrimitive?.content ?: "",
        )
    }
}
