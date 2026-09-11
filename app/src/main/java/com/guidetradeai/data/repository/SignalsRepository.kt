package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AnalysisRequest
import com.guidetradeai.domain.model.AnalysisResult
import com.guidetradeai.domain.model.Signal
import com.guidetradeai.domain.model.SignalFilter
import com.guidetradeai.domain.model.WatchlistItem

class SignalsRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun getSignals(filter: SignalFilter): Result<List<Signal>> {
        return try {
            val body = buildMap {
                filter.market?.let { put("market", it) }
                filter.timeframe?.let { put("timeframe", it) }
                filter.direction?.let { put("direction", it) }
                if (filter.search.isNotBlank()) put("search", filter.search)
            }
            val response = supabase.supabase.functions.invoke(
                function = "guide-trade-agent",
                body = body,
            )
            val json = response.bodyOrNull()
            val signals = parseSignals(json)
            Result.Success(signals)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load signals")
        }
    }

    suspend fun analyze(request: AnalysisRequest): Result<AnalysisResult> {
        return try {
            val body = buildMap {
                put("market", request.market)
                put("symbol", request.symbol)
                put("timeframe", request.timeframe)
                put("analysisType", request.analysisType)
            }
            val response = supabase.supabase.functions.invoke(
                function = "guide-trade-agent",
                body = body,
            )
            val json = response.bodyOrNull()
            val signal = parseSignalFromJson(json)
            Result.Success(AnalysisResult(signal = signal, content = json))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Analysis failed")
        }
    }

    suspend fun getWatchlist(): Result<List<WatchlistItem>> {
        return try {
            val userId = supabase.supabase.auth.currentUserOrNull()?.id ?: return Result.Error("Not authenticated")
            val response = supabase.supabase.postgrest
                .from("watchlist")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
            val items = response.decodeList<WatchlistItem>()
            Result.Success(items)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load watchlist")
        }
    }

    suspend fun addToWatchlist(symbol: String, market: String, timeframe: String): Result<Unit> {
        return try {
            val userId = supabase.supabase.auth.currentUserOrNull()?.id ?: return Result.Error("Not authenticated")
            val item = WatchlistItem(
                id = userId + "_" + symbol + "_" + market + "_" + timeframe,
                userId = userId,
                symbol = symbol,
                market = market,
                timeframe = timeframe,
            )
            supabase.supabase.postgrest
                .from("watchlist")
                .insert(item)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add to watchlist")
        }
    }

    suspend fun removeFromWatchlist(itemId: String): Result<Unit> {
        return try {
            supabase.supabase.postgrest
                .from("watchlist")
                .delete {
                    filter {
                        eq("id", itemId)
                    }
                }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove from watchlist")
        }
    }

    private fun parseSignals(json: String?): List<Signal> {
        if (json == null) return emptyList()
        return try {
            val elements = kotlinx.serialization.json.Json.parseToJsonElement(json)
            if (elements is kotlinx.serialization.json.JsonArray) {
                elements.map { elem ->
                    val obj = elem as kotlinx.serialization.json.JsonObject
                    Signal(
                        id = obj["id"]?.toString()?.trim('"') ?: "",
                        symbol = obj["symbol"]?.toString()?.trim('"') ?: "",
                        name = obj["name"]?.toString()?.trim('"') ?: "",
                        market = obj["market"]?.toString()?.trim('"') ?: "crypto",
                        timeframe = obj["timeframe"]?.toString()?.trim('"') ?: "1H",
                        direction = obj["direction"]?.toString()?.trim('"') ?: obj["signal"]?.toString()?.trim('"') ?: "neutral",
                        strength = obj["strength"]?.toString()?.toDoubleOrNull() ?: obj["score"]?.toString()?.toDoubleOrNull() ?: 0.0,
                        entry = obj["entry"]?.toString()?.toDoubleOrNull(),
                        invalidation = obj["invalidation"]?.toString()?.toDoubleOrNull(),
                        target1 = obj["target1"]?.toString()?.toDoubleOrNull(),
                        target2 = obj["target2"]?.toString()?.toDoubleOrNull(),
                        target3 = obj["target3"]?.toString()?.toDoubleOrNull(),
                        currentPrice = obj["currentPrice"]?.toString()?.toDoubleOrNull() ?: obj["price"]?.toString()?.toDoubleOrNull(),
                        summary = obj["summary"]?.toString()?.trim('"'),
                        analysis = obj["analysis"]?.toString()?.trim('"'),
                        risk = obj["risk"]?.toString()?.trim('"'),
                        marketContext = obj["marketContext"]?.toString()?.trim('"') ?: obj["market_context"]?.toString()?.trim('"'),
                        technicalInfo = obj["technicalInfo"]?.toString()?.trim('"') ?: obj["technical_info"]?.toString()?.trim('"'),
                        why = obj["why"]?.toString()?.trim('"'),
                        source = obj["source"]?.toString()?.trim('"') ?: "NORTH7",
                        timestamp = obj["timestamp"]?.toString()?.trim('"') ?: "",
                        updatedAt = obj["updatedAt"]?.toString()?.trim('"') ?: "",
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
            val obj = kotlinx.serialization.json.Json.parseToJsonElement(json) as kotlinx.serialization.json.JsonObject
            Signal(
                id = obj["id"]?.toString()?.trim('"') ?: "",
                symbol = obj["symbol"]?.toString()?.trim('"') ?: "",
                name = obj["name"]?.toString()?.trim('"') ?: "",
                market = obj["market"]?.toString()?.trim('"') ?: "crypto",
                timeframe = obj["timeframe"]?.toString()?.trim('"') ?: "1H",
                direction = obj["direction"]?.toString()?.trim('"') ?: obj["signal"]?.toString()?.trim('"') ?: "neutral",
                strength = obj["strength"]?.toString()?.toDoubleOrNull() ?: obj["score"]?.toString()?.toDoubleOrNull() ?: 0.0,
                entry = obj["entry"]?.toString()?.toDoubleOrNull(),
                invalidation = obj["invalidation"]?.toString()?.toDoubleOrNull(),
                target1 = obj["target1"]?.toString()?.toDoubleOrNull(),
                target2 = obj["target2"]?.toString()?.toDoubleOrNull(),
                target3 = obj["target3"]?.toString()?.toDoubleOrNull(),
                currentPrice = obj["currentPrice"]?.toString()?.toDoubleOrNull() ?: obj["price"]?.toString()?.toDoubleOrNull(),
                summary = obj["summary"]?.toString()?.trim('"'),
                analysis = obj["analysis"]?.toString()?.trim('"'),
                risk = obj["risk"]?.toString()?.trim('"'),
                marketContext = obj["marketContext"]?.toString()?.trim('"') ?: obj["market_context"]?.toString()?.trim('"'),
                technicalInfo = obj["technicalInfo"]?.toString()?.trim('"') ?: obj["technical_info"]?.toString()?.trim('"'),
                why = obj["why"]?.toString()?.trim('"'),
                source = obj["source"]?.toString()?.trim('"') ?: "NORTH7",
                timestamp = obj["timestamp"]?.toString()?.trim('"') ?: "",
                updatedAt = obj["updatedAt"]?.toString()?.trim('"') ?: "",
            )
        } catch (e: Exception) {
            null
        }
    }
}
