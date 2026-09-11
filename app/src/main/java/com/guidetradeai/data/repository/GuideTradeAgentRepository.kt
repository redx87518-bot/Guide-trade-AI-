package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import com.guidetradeai.domain.model.AgentSession
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

class GuideTradeAgentRepository(
    private val supabase: SupabaseClient,
) {
    suspend fun sendRequest(request: AgentRequest): Result<AgentResponse> {
        return try {
            val body = buildJsonObject {
                put("goal", JsonPrimitive(request.goal))
                request.sessionId?.let { put("session_id", JsonPrimitive(it)) }
                                request.market?.let { put("market", JsonPrimitive(it)) }
                request.symbol?.let { put("symbol", JsonPrimitive(it)) }
                request.timeframe?.let { put("timeframe", JsonPrimitive(it)) }
                request.feature?.let { put("feature", JsonPrimitive(it)) }
                request.query?.let { put("query", JsonPrimitive(it)) }
            }
            val response = supabase.functions.invoke("agent-orchestrator", body = body)
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(error)
            }
            val agentResponse = parseAgentResponse(jsonObject)
            Result.success(agentResponse)
        } catch (e: Exception) {
            Result.error("Agent request failed: ${e.message}")
        }
    }

    suspend fun listSymbols(provider: String, market: String): Result<List<SymbolItem>> {
        return try {
            val body = buildJsonObject {
                put("provider", JsonPrimitive(provider))
                put("feature", JsonPrimitive("list_symbols"))
                put("market", JsonPrimitive(market))
            }
            val response = supabase.functions.invoke("agent-orchestrator", body = body)
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(error)
            }
            val symbols = mutableListOf<SymbolItem>()
            jsonObject["symbols"]?.jsonObject?.forEach { (key, value) ->
                val name = value.jsonObject["name"]?.jsonPrimitive?.content ?: key
                symbols.add(SymbolItem(symbol = key, name = name, market = market))
            }
            jsonObject["data"]?.let { dataElement ->
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
            Result.error("Failed to load symbols: ${e.message}")
        }
    }

    suspend fun listSessions(userId: String): Result<List<AgentSession>> {
        return try {
            val response = supabase.functions.invoke("agent-orchestrator", body = buildJsonObject {
                put("goal", JsonPrimitive("list_sessions"))
                put("user_id", JsonPrimitive(userId))
            })
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(error)
            }
            val sessions = mutableListOf<AgentSession>()
            jsonObject["sessions"]?.let { sessionsElement ->
                if (sessionsElement is kotlinx.serialization.json.JsonArray) {
                    sessionsElement.forEach { session ->
                        val obj = session.jsonObject
                        sessions.add(
                            AgentSession(
                                id = obj["id"]?.jsonPrimitive?.content ?: "",
                                title = obj["title"]?.jsonPrimitive?.content ?: "",
                                                                createdAt = obj["created_at"]?.jsonPrimitive?.content,
                                updatedAt = obj["updated_at"]?.jsonPrimitive?.content,
                            )
                        )
                    }
                }
            }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.error("Failed to load agent sessions: ${e.message}")
        }
    }

    private fun parseAgentResponse(json: JsonObject): AgentResponse {
        val toolCalls = mutableListOf<com.guidetradeai.domain.model.AgentToolCall>()
        json["tool_calls"]?.jsonObject?.forEach { (name, value) ->
            val obj = value.jsonObject
            toolCalls.add(
                com.guidetradeai.domain.model.AgentToolCall(
                    name = name,
                    status = obj["status"]?.jsonPrimitive?.content ?: "running",
                    startedAt = obj["started_at"]?.jsonPrimitive?.content,
                    finishedAt = obj["finished_at"]?.jsonPrimitive?.content,
                )
            )
        }

        val tasks = mutableListOf<com.guidetradeai.domain.model.AgentTask>()
        json["tasks"]?.let { tasksElement ->
            if (tasksElement is kotlinx.serialization.json.JsonArray) {
                tasksElement.forEach { task ->
                    val obj = task.jsonObject
                    tasks.add(
                        com.guidetradeai.domain.model.AgentTask(
                            id = obj["id"]?.jsonPrimitive?.content ?: "",
                            description = obj["description"]?.jsonPrimitive?.content ?: "",
                            status = obj["status"]?.jsonPrimitive?.content ?: "",
                            createdAt = obj["created_at"]?.jsonPrimitive?.content,
                        )
                    )
                }
            }
        }

        return AgentResponse(
            sessionId = json["session_id"]?.jsonPrimitive?.content,
            content = json["content"]?.jsonPrimitive?.content,
            summary = json["summary"]?.jsonPrimitive?.content,
                        market = json["market"]?.jsonPrimitive?.content,
            symbol = json["symbol"]?.jsonPrimitive?.content,
            timeframe = json["timeframe"]?.jsonPrimitive?.content,
            toolsUsed = json["tools_used"]?.let { toolsElement ->
                if (toolsElement is kotlinx.serialization.json.JsonArray) {
                    toolsElement.mapNotNull { it.jsonPrimitive?.content }
                } else {
                    emptyList()
                }
            } ?: emptyList(),
            toolCalls = toolCalls,
            tasks = tasks,
            marketData = json["market_data"]?.let { parseMarketData(it.jsonObject) },
            timestamp = json["timestamp"]?.jsonPrimitive?.content,
        )
    }

    private fun parseMarketData(json: JsonObject): com.guidetradeai.domain.model.MarketDataResponse {
        return com.guidetradeai.domain.model.MarketDataResponse(
                        market = json["market"]?.jsonPrimitive?.content ?: "",
            symbol = json["symbol"]?.jsonPrimitive?.content ?: "",
            name = json["name"]?.jsonPrimitive?.content ?: "",
            timestamp = json["timestamp"]?.jsonPrimitive?.content ?: "",
            price = json["price"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            change = json["change"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            changePercent = json["change_percent"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            high = json["high"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            low = json["low"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            signal = json["signal"]?.jsonPrimitive?.content ?: "",
            score = json["score"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            oscillator = json["oscillator"]?.jsonPrimitive?.content ?: "",
            movingAverage = json["moving_average"]?.jsonPrimitive?.content ?: "",
            rsi = json["rsi"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            macd = json["macd"]?.jsonPrimitive?.content ?: "",
            stochastic = json["stochastic"]?.jsonPrimitive?.content ?: "",
            cci = json["cci"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            williamsR = json["williams_r"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            momentum = json["momentum"]?.jsonPrimitive?.content ?: "",
            sma = json["sma"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            ema = json["ema"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            barStatus = json["bar_status"]?.jsonPrimitive?.content ?: "",
            sentiment = json["sentiment"]?.jsonPrimitive?.content ?: "",
            trend = json["trend"]?.jsonPrimitive?.content ?: "",
            news = parseNewsItems(json["news"]),
            chartData = parseChartPoints(json["chart_data"]),
            metadata = json["metadata"]?.jsonObject?.let { meta ->
                meta.mapValues { it.value.jsonPrimitive.content }
            } ?: emptyMap(),
        )
    }

    private fun parseNewsItems(json: kotlinx.serialization.json.JsonElement?): List<com.guidetradeai.domain.model.NewsItem> {
        if (json == null || json !is kotlinx.serialization.json.JsonArray) return emptyList()
        return json.mapNotNull { item ->
            val obj = item.jsonObject
            com.guidetradeai.domain.model.NewsItem(
                title = obj["title"]?.jsonPrimitive?.content ?: "",
                summary = obj["summary"]?.jsonPrimitive?.content ?: "",
                timestamp = obj["timestamp"]?.jsonPrimitive?.content ?: "",
                source = obj["source"]?.jsonPrimitive?.content ?: "",
            )
        }
    }

    private fun parseChartPoints(json: kotlinx.serialization.json.JsonElement?): List<com.guidetradeai.domain.model.ChartPoint> {
        if (json == null || json !is kotlinx.serialization.json.JsonArray) return emptyList()
        return json.mapNotNull { item ->
            val obj = item.jsonObject
            com.guidetradeai.domain.model.ChartPoint(
                timestamp = obj["timestamp"]?.jsonPrimitive?.content ?: "",
                value = obj["value"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                volume = obj["volume"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            )
        }
    }
}
