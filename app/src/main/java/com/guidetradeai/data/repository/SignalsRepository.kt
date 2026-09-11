package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AnalysisRequest
import com.guidetradeai.domain.model.AnalysisResult
import com.guidetradeai.domain.model.Signal
import com.guidetradeai.domain.model.SignalFilter
import com.guidetradeai.domain.model.WatchlistItem
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class SignalsRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun getSignals(filter: SignalFilter): Result<List<Signal>> {
        return try {
            val body = JsonObject(buildMap {
                filter.market?.let { put("market", JsonPrimitive(it)) }
                filter.timeframe?.let { put("timeframe", JsonPrimitive(it)) }
                filter.direction?.let { put("direction", JsonPrimitive(it)) }
                if (filter.search.isNotBlank()) put("search", JsonPrimitive(filter.search))
            })
            val response = supabase.functions.invoke("guide-trade-agent", body = body)
            val data = response.bodyAsText()
            val signals = parseSignals(data)
            Result.Success(signals)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load signals")
        }
    }

    suspend fun analyze(request: AnalysisRequest): Result<AnalysisResult> {
        return try {
            val body = JsonObject(buildMap {
                put("market", JsonPrimitive(request.market))
                put("symbol", JsonPrimitive(request.symbol))
                put("timeframe", JsonPrimitive(request.timeframe))
                put("analysisType", JsonPrimitive(request.analysisType))
            })
            val response = supabase.functions.invoke("guide-trade-agent", body = body)
            val data = response.bodyAsText()
            val signal = parseSignalFromJson(data)
            Result.Success(AnalysisResult(signal = signal, content = data))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Analysis failed")
        }
    }

    suspend fun getWatchlist(): Result<List<WatchlistItem>> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return Result.Error("Not authenticated")
            val result = supabase.postgrest
                .from("watchlist")
                .select {}
                .decodeList<WatchlistItem>()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load watchlist")
        }
    }

    suspend fun addToWatchlist(symbol: String, market: String, timeframe: String): Result<Unit> {
        return try {
            val userId = supabase.auth.currentUserOrNull()?.id ?: return Result.Error("Not authenticated")
            val item = WatchlistItem(
                id = userId + "_" + symbol + "_" + market + "_" + timeframe,
                userId = userId,
                symbol = symbol,
                market = market,
                timeframe = timeframe,
            )
            supabase.postgrest
                .from("watchlist")
                .insert(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add to watchlist")
        }
    }

    suspend fun removeFromWatchlist(itemId: String): Result<Unit> {
        return try {
            supabase.postgrest
                .from("watchlist")
                .delete {}
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove from watchlist")
        }
    }

    private fun parseSignals(json: String?): List<Signal> {
        if (json == null) return emptyList()
        return try {
            val elements = Json.parseToJsonElement(json)
            if (elements is JsonObject) {
                val dataArray = elements["data"]
                if (dataArray is JsonObject) {
                    val signalsArray = dataArray["signals"]
                    if (signalsArray is kotlinx.serialization.json.JsonArray) {
                        signalsArray.map { elem ->
                            val obj = elem as JsonObject
                            Signal(
                                id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                                symbol = obj["symbol"]?.jsonPrimitive?.contentOrNull ?: "",
                                name = obj["name"]?.jsonPrimitive?.contentOrNull ?: "",
                                market = obj["market"]?.jsonPrimitive?.contentOrNull ?: "crypto",
                                timeframe = obj["timeframe"]?.jsonPrimitive?.contentOrNull ?: "1H",
                                direction = obj["direction"]?.jsonPrimitive?.contentOrNull ?: obj["signal"]?.jsonPrimitive?.contentOrNull ?: "neutral",
                                strength = obj["strength"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: obj["score"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: 0.0,
                                entry = obj["entry"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                                invalidation = obj["invalidation"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                                target1 = obj["target1"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                                target2 = obj["target2"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                                target3 = obj["target3"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                                currentPrice = (obj["currentPrice"]?.jsonPrimitive?.contentOrNull ?: obj["price"]?.jsonPrimitive?.contentOrNull)?.toDoubleOrNull(),
                                summary = obj["summary"]?.jsonPrimitive?.contentOrNull,
                                analysis = obj["analysis"]?.jsonPrimitive?.contentOrNull,
                                risk = obj["risk"]?.jsonPrimitive?.contentOrNull,
                                marketContext = (obj["marketContext"]?.jsonPrimitive?.contentOrNull ?: obj["market_context"]?.jsonPrimitive?.contentOrNull),
                                technicalInfo = (obj["technicalInfo"]?.jsonPrimitive?.contentOrNull ?: obj["technical_info"]?.jsonPrimitive?.contentOrNull),
                                why = obj["why"]?.jsonPrimitive?.contentOrNull,
                                source = obj["source"]?.jsonPrimitive?.contentOrNull ?: "NORTH7",
                                timestamp = obj["timestamp"]?.jsonPrimitive?.contentOrNull ?: "",
                                updatedAt = obj["updatedAt"]?.jsonPrimitive?.contentOrNull ?: "",
                            )
                        }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            } else if (elements is kotlinx.serialization.json.JsonArray) {
                elements.map { elem ->
                    val obj = elem as JsonObject
                    Signal(
                        id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                        symbol = obj["symbol"]?.jsonPrimitive?.contentOrNull ?: "",
                        name = obj["name"]?.jsonPrimitive?.contentOrNull ?: "",
                        market = obj["market"]?.jsonPrimitive?.contentOrNull ?: "crypto",
                        timeframe = obj["timeframe"]?.jsonPrimitive?.contentOrNull ?: "1H",
                        direction = obj["direction"]?.jsonPrimitive?.contentOrNull ?: obj["signal"]?.jsonPrimitive?.contentOrNull ?: "neutral",
                        strength = obj["strength"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: obj["score"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: 0.0,
                        entry = obj["entry"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                        invalidation = obj["invalidation"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                        target1 = obj["target1"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                        target2 = obj["target2"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                        target3 = obj["target3"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                        currentPrice = (obj["currentPrice"]?.jsonPrimitive?.contentOrNull ?: obj["price"]?.jsonPrimitive?.contentOrNull)?.toDoubleOrNull(),
                        summary = obj["summary"]?.jsonPrimitive?.contentOrNull,
                        analysis = obj["analysis"]?.jsonPrimitive?.contentOrNull,
                        risk = obj["risk"]?.jsonPrimitive?.contentOrNull,
                        marketContext = (obj["marketContext"]?.jsonPrimitive?.contentOrNull ?: obj["market_context"]?.jsonPrimitive?.contentOrNull),
                        technicalInfo = (obj["technicalInfo"]?.jsonPrimitive?.contentOrNull ?: obj["technical_info"]?.jsonPrimitive?.contentOrNull),
                        why = obj["why"]?.jsonPrimitive?.contentOrNull,
                        source = obj["source"]?.jsonPrimitive?.contentOrNull ?: "NORTH7",
                        timestamp = obj["timestamp"]?.jsonPrimitive?.contentOrNull ?: "",
                        updatedAt = obj["updatedAt"]?.jsonPrimitive?.contentOrNull ?: "",
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseSignalFromJson(json: String?): Signal? {
        if (json == null) return null
        return try {
            val obj = Json.parseToJsonElement(json) as JsonObject
            Signal(
                id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                symbol = obj["symbol"]?.jsonPrimitive?.contentOrNull ?: "",
                name = obj["name"]?.jsonPrimitive?.contentOrNull ?: "",
                market = obj["market"]?.jsonPrimitive?.contentOrNull ?: "crypto",
                timeframe = obj["timeframe"]?.jsonPrimitive?.contentOrNull ?: "1H",
                direction = obj["direction"]?.jsonPrimitive?.contentOrNull ?: obj["signal"]?.jsonPrimitive?.contentOrNull ?: "neutral",
                strength = obj["strength"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: obj["score"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: 0.0,
                entry = obj["entry"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                invalidation = obj["invalidation"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                target1 = obj["target1"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                target2 = obj["target2"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                target3 = obj["target3"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                currentPrice = (obj["currentPrice"]?.jsonPrimitive?.contentOrNull ?: obj["price"]?.jsonPrimitive?.contentOrNull)?.toDoubleOrNull(),
                summary = obj["summary"]?.jsonPrimitive?.contentOrNull,
                analysis = obj["analysis"]?.jsonPrimitive?.contentOrNull,
                risk = obj["risk"]?.jsonPrimitive?.contentOrNull,
                marketContext = (obj["marketContext"]?.jsonPrimitive?.contentOrNull ?: obj["market_context"]?.jsonPrimitive?.contentOrNull),
                technicalInfo = (obj["technicalInfo"]?.jsonPrimitive?.contentOrNull ?: obj["technical_info"]?.jsonPrimitive?.contentOrNull),
                why = obj["why"]?.jsonPrimitive?.contentOrNull,
                source = obj["source"]?.jsonPrimitive?.contentOrNull ?: "NORTH7",
                timestamp = obj["timestamp"]?.jsonPrimitive?.contentOrNull ?: "",
                updatedAt = obj["updatedAt"]?.jsonPrimitive?.contentOrNull ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseWatchlist(json: String?): List<WatchlistItem> {
        if (json == null) return emptyList()
        return try {
            val elements = Json.parseToJsonElement(json)
            if (elements is kotlinx.serialization.json.JsonArray) {
                elements.map { elem ->
                    val obj = elem as JsonObject
                    WatchlistItem(
                        id = obj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                        userId = obj["user_id"]?.jsonPrimitive?.contentOrNull ?: "",
                        symbol = obj["symbol"]?.jsonPrimitive?.contentOrNull ?: "",
                        market = obj["market"]?.jsonPrimitive?.contentOrNull ?: "crypto",
                        timeframe = obj["timeframe"]?.jsonPrimitive?.contentOrNull ?: "1H",
                        addedAt = obj["created_at"]?.jsonPrimitive?.contentOrNull ?: "",
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
