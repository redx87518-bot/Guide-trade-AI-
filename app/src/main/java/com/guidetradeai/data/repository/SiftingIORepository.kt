package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.SymbolItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SiftingIORepository(private val supabase: SupabaseClient) {

    suspend fun query(
        market: String,
        symbol: String,
        timeframe: String,
        feature: String,
        query: String? = null,
    ): Result<JsonObject> {
        return try {
            val body = buildJsonObject {
                put("provider", JsonPrimitive("siftingio"))
                put("feature", JsonPrimitive(feature))
                put("market", JsonPrimitive(market))
                put("symbol", JsonPrimitive(symbol))
                put("timeframe", JsonPrimitive(timeframe))
                query?.let { put("query", JsonPrimitive(it)) }
            }
            val response = supabase.functions.invoke("siftingio-market", body = body)
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(error)
            }
            val result = jsonObject["result"]?.jsonObject
                ?: return Result.error("Empty result from SiftingIO")
            Result.success(result)
        } catch (e: Exception) {
            Result.error("SiftingIO request failed: ${e.message}")
        }
    }

    suspend fun listSymbols(market: String): Result<List<SymbolItem>> {
        return try {
            val body = buildJsonObject {
                put("provider", JsonPrimitive("siftingio"))
                put("feature", JsonPrimitive("list_symbols"))
                put("market", JsonPrimitive(market))
            }
            val response = supabase.functions.invoke("siftingio-market", body = body)
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
                if (dataElement is kotlinx.serialization.json.JsonArray) {
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
            Result.error("Failed to load SiftingIO symbols: ${e.message}")
        }
    }
}
