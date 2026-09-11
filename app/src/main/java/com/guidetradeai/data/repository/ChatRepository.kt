package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.data.remote.ChatMessageData
import com.guidetradeai.data.remote.ChatSessionData
import com.guidetradeai.domain.Result

class ChatRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun getSessions(): Result<List<ChatSessionData>> {
        return try {
            val result = supabase.postgrest.from("chat_sessions").select {}.decodeList<ChatSessionData>()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load sessions")
        }
    }

    suspend fun createSession(title: String = "New Chat"): Result<ChatSessionData> {
        return try {
            val id = java.util.UUID.randomUUID().toString()
            supabase.postgrest.from("chat_sessions").insert(kotlinx.serialization.json.buildJsonObject {
                put("id", kotlinx.serialization.json.JsonPrimitive(id))
                put("title", kotlinx.serialization.json.JsonPrimitive(title))
            })
            Result.Success(ChatSessionData(id = id, title = title))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create session")
        }
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            supabase.postgrest.from("chat_sessions").delete { filter { eq("id", sessionId) } }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete session")
        }
    }

    suspend fun getMessages(sessionId: String): Result<List<ChatMessageData>> {
        return try {
            val result = supabase.postgrest.from("chat_messages").select { filter { eq("session_id", sessionId) } }.decodeList<ChatMessageData>()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load messages")
        }
    }

    suspend fun saveMessage(message: ChatMessageData): Result<ChatMessageData> {
        return try {
            supabase.postgrest.from("chat_messages").insert(message)
            Result.Success(message)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save message")
        }
    }
}
