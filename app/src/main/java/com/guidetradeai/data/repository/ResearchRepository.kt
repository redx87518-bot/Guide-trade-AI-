package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ResearchResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.eq
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

class ResearchRepository(private val supabase: SupabaseClient) {

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
                ?: return Result.error("Empty research response")
            val result = ResearchResult(
                id = parsed["id"]?.jsonPrimitive?.contentOrNull ?: "",
                userId = userId,
                title = query,
                query = query,
                response = summary,
                asset = null,
                createdAt = Instant.now().atZone(ZoneId.of("UTC")).toString(),
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to generate research")
        }
    }

    suspend fun getResearchHistory(userId: String): Result<List<ResearchResult>> {
        return try {
            val result = supabase.postgrest.from("research_results")
                .select { filter { eq("user_id", userId) } }
            val rows = result.decodeList<JsonObject>()
            val results = rows.map { row ->
                ResearchResult(
                    id = row["id"]?.jsonPrimitive?.content ?: "",
                    userId = row["user_id"]?.jsonPrimitive?.content ?: "",
                    title = row["title"]?.jsonPrimitive?.content ?: "",
                    query = row["query"]?.jsonPrimitive?.content ?: "",
                    asset = row["asset"]?.jsonPrimitive?.content,
                    response = row["response"]?.jsonPrimitive?.content ?: "",
                    createdAt = row["created_at"]?.jsonPrimitive?.content ?: "",
                )
            }.sortedByDescending { it.createdAt }
            Result.success(results)
        } catch (e: Exception) {
            Result.error(e.message ?: "Failed to load research history")
        }
    }
}
