package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.MarketIntelligenceRequest
import com.guidetradeai.domain.model.MarketIntelligenceResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MarketIntelligenceRepository(
    private val supabase: SupabaseClient,
) {
    suspend fun queryProvider(request: MarketIntelligenceRequest): Result<MarketIntelligenceResponse> {
        return try {
            val body = buildJsonObject {
                put("provider", JsonPrimitive(request.provider))
                put("feature", JsonPrimitive(request.feature))
                request.market?.let { put("market", JsonPrimitive(it)) }
                request.symbol?.let { put("symbol", JsonPrimitive(it)) }
                request.timeframe?.let { put("timeframe", JsonPrimitive(it)) }
                request.query?.let { put("query", JsonPrimitive(it)) }
            }
            val response = supabase.functions.invoke("market-intelligence", body = body)
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(error)
            }
            val result = jsonObject["result"]?.jsonObject
            Result.success(
                MarketIntelligenceResponse(
                    provider = request.provider,
                    feature = request.feature,
                    market = request.market,
                    symbol = request.symbol,
                    timeframe = request.timeframe,
                    result = result,
                )
            )
        } catch (e: Exception) {
            Result.error("Failed to query provider: ${e.message}")
        }
    }
}
