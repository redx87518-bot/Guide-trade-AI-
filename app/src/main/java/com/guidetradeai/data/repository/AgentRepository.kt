package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse

class AgentRepository(private val supabase: com.guidetradeai.data.remote.SupabaseClient = com.guidetradeai.data.remote.SupabaseClient) {

    suspend fun sendMessage(sessionId: String, message: String): Result<AgentResponse> {
        return try {
            Result.Success(AgentResponse(content = "Agent stub response"))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Agent request failed")
        }
    }
}
