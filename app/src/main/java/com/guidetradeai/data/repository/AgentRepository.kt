package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class AgentRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun sendMessage(sessionId: String, message: String): Result<AgentResponse> {
        return try {
            val body = JsonObject(buildMap {
                put("action", JsonPrimitive("agent"))
                put("sessionId", JsonPrimitive(sessionId))
                put("message", JsonPrimitive(message))
            })
            val response = supabase.functions.invoke("guide-trade-agent", body = body)
            val data = response.bodyAsText()
            val parsed = parseAgentResponse(data)
            Result.Success(parsed)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Agent request failed")
        }
    }

    private fun parseAgentResponse(json: String?): AgentResponse {
        if (json == null) return AgentResponse(content = "No response from agent.")
        return try {
            val obj = Json.parseToJsonElement(json)
            if (obj is JsonObject) {
                AgentResponse(
                    sessionId = obj["sessionId"]?.jsonPrimitive?.contentOrNull,
                    content = obj["content"]?.jsonPrimitive?.contentOrNull,
                    summary = obj["summary"]?.jsonPrimitive?.contentOrNull,
                    provider = obj["provider"]?.jsonPrimitive?.contentOrNull,
                    market = obj["market"]?.jsonPrimitive?.contentOrNull,
                    symbol = obj["symbol"]?.jsonPrimitive?.contentOrNull,
                    timeframe = obj["timeframe"]?.jsonPrimitive?.contentOrNull,
                    toolsUsed = emptyList(),
                    toolCalls = emptyList(),
                    tasks = emptyList(),
                    marketData = null,
                    timestamp = obj["timestamp"]?.jsonPrimitive?.contentOrNull,
                )
            } else {
                AgentResponse(content = json)
            }
        } catch (e: Exception) {
            AgentResponse(content = json)
        }
    }
}
