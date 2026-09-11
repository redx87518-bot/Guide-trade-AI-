package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ResearchResult
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.ZoneId

class ResearchRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun generateResearch(query: String, userId: String): Result<ResearchResult> {
        return try {
            val body = buildJsonObject {
                put("action", JsonPrimitive("research"))
                put("user_query", JsonPrimitive(query))
                put("user_id", JsonPrimitive(userId))
            }
            val response = supabase.functions.invoke("agent-orchestrator", body = body)
            val data = response.bodyAsText()
            val parsed = Json.parseToJsonElement(data).jsonObject
            val summary = parsed["summary"]?.jsonPrimitive?.contentOrNull
                ?: parsed["content"]?.jsonPrimitive?.contentOrNull
                ?: return Result.Error("Empty research response")
            val result = ResearchResult(
                id = parsed["id"]?.jsonPrimitive?.contentOrNull ?: "",
                userId = userId,
                title = query,
                query = query,
                response = summary,
                asset = null,
                createdAt = Instant.now().atZone(ZoneId.of("UTC")).toString(),
            )
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to generate research")
        }
    }

    suspend fun getResearchHistory(userId: String): Result<List<ResearchResult>> {
        return try {
            Result.Success(emptyList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load research history")
        }
    }
}
