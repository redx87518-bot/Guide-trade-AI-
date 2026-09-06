package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.MarketIntelligenceRequest
import com.guidetradeai.domain.model.MarketIntelligenceResponse
import com.guidetradeai.domain.model.SymbolItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
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

    suspend fun listSymbols(provider: String, market: String): Result<List<SymbolItem>> {
        return try {
            val body = buildJsonObject {
                put("provider", JsonPrimitive(provider))
                put("feature", JsonPrimitive("list_symbols"))
                put("market", JsonPrimitive(market))
            }
            val response = supabase.functions.invoke("market-intelligence", body = body)
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(error)
            }
            val result = jsonObject["result"]?.jsonObject
            val symbols = mutableListOf<SymbolItem>()
            
            result?.get("symbols")?.jsonObject?.forEach { (key, value) ->
                val name = value.jsonObject["name"]?.jsonPrimitive?.content ?: key
                symbols.add(SymbolItem(symbol = key, name = name, market = market))
            }
            
            result?.get("data")?.let { dataElement ->
                if (dataElement is JsonArray) {
                    dataElement.forEach { item ->
                        val obj = item.jsonObject
                        val symbol = obj["symbol"]?.jsonPrimitive?.content ?: obj["ticker"]?.jsonPrimitive?.content ?: ""
                        val name = obj["name"]?.jsonPrimitive?.content ?: obj["description"]?.jsonPrimitive?.content ?: symbol
                        if (symbol.isNotBlank()) {
                            symbols.add(SymbolItem(symbol = symbol, name = name, market = market))
                        }
                    }
                }
            }
            
            Result.success(symbols)
        } catch (e: Exception) {
            Result.error("Failed to load symbols: ${e.message}")
        }
    }
}
