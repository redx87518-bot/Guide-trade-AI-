package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.data.remote.ChatMessageData
import com.guidetradeai.data.remote.ChatSessionData
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AgentRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun sendMessage(sessionId: String, message: String): Result<ChatMessageData> {
        return try {
            val userId = supabase.client.auth.currentSessionOrNull()?.user?.id ?: return Result.Error("Not authenticated")
            val body = buildJsonObject {
                put("session_id", sessionId)
                put("user_id", userId)
                put("message", message)
            }
            val response = supabase.client.functions.invoke("guide-trade-agent", body = body)
            val responseText = response.bodyAsText()
            Result.Success(
                ChatMessageData(
                    session_id = sessionId,
                    user_id = userId,
                    role = "assistant",
                    content = responseText,
                )
            )
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send message")
        }
    }
}
