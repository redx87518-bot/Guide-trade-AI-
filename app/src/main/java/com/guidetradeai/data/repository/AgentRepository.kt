package com.guidetradeai.data.repository

import com.guidetradeai.util.ErrorSanitizer

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import com.guidetradeai.domain.model.Signal
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
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
            Result.Error(ErrorSanitizer.userFriendly(e.message ?: "Agent request failed"))
        }
    }

    private fun parseAgentResponse(json: String?): AgentResponse {
        if (json == null) return AgentResponse(content = "No response from agent.")
        return try {
            val root = Json.parseToJsonElement(json)
            val obj = if (root is JsonObject) root else JsonObject(emptyMap())
            val dataObj = (obj["data"] as? JsonObject) ?: obj

            val content = dataObj["content"]?.jsonPrimitive?.contentOrNull
                ?: obj["content"]?.jsonPrimitive?.contentOrNull

            val summary = dataObj["summary"]?.jsonPrimitive?.contentOrNull
                ?: obj["summary"]?.jsonPrimitive?.contentOrNull

            val signals = mutableListOf<Signal>()
            val signalsArray = dataObj["signals"] as? JsonArray
                ?: obj["signals"] as? JsonArray
            if (signalsArray != null) {
                signalsArray.forEach { elem ->
                    val signalObj = elem as? JsonObject ?: return@forEach
                    signals.add(
                        Signal(
                            id = signalObj["id"]?.jsonPrimitive?.contentOrNull ?: "",
                            symbol = signalObj["symbol"]?.jsonPrimitive?.contentOrNull ?: "",
                            name = signalObj["name"]?.jsonPrimitive?.contentOrNull ?: "",
                            market = signalObj["market"]?.jsonPrimitive?.contentOrNull ?: "crypto",
                            timeframe = signalObj["timeframe"]?.jsonPrimitive?.contentOrNull ?: "1H",
                            direction = signalObj["direction"]?.jsonPrimitive?.contentOrNull ?: signalObj["signal"]?.jsonPrimitive?.contentOrNull ?: "neutral",
                            strength = signalObj["strength"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull()
                                ?: signalObj["score"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull() ?: 0.0,
                            entry = signalObj["entry"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                            invalidation = signalObj["invalidation"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                            target1 = signalObj["target1"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                            target2 = signalObj["target2"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                            target3 = signalObj["target3"]?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                            currentPrice = (signalObj["currentPrice"]?.jsonPrimitive?.contentOrNull
                                ?: signalObj["price"]?.jsonPrimitive?.contentOrNull)?.toDoubleOrNull(),
                            summary = signalObj["summary"]?.jsonPrimitive?.contentOrNull,
                            analysis = signalObj["analysis"]?.jsonPrimitive?.contentOrNull,
                            risk = signalObj["risk"]?.jsonPrimitive?.contentOrNull,
                            marketContext = (signalObj["marketContext"]?.jsonPrimitive?.contentOrNull
                                ?: signalObj["market_context"]?.jsonPrimitive?.contentOrNull),
                            technicalInfo = (signalObj["technicalInfo"]?.jsonPrimitive?.contentOrNull
                                ?: signalObj["technical_info"]?.jsonPrimitive?.contentOrNull),
                            why = signalObj["why"]?.jsonPrimitive?.contentOrNull,
                            source = signalObj["source"]?.jsonPrimitive?.contentOrNull ?: "NORTH7",
                            timestamp = signalObj["timestamp"]?.jsonPrimitive?.contentOrNull ?: "",
                            updatedAt = signalObj["updatedAt"]?.jsonPrimitive?.contentOrNull ?: "",
                        )
                    )
                }
            }

            val marketData = com.guidetradeai.domain.model.MarketData(
                price = (dataObj["price"] ?: obj["price"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                change = (dataObj["change"] ?: obj["change"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                changePercent = (dataObj["changePercent"] ?: obj["changePercent"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                high = (dataObj["high"] ?: obj["high"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                low = (dataObj["low"] ?: obj["low"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                volume = (dataObj["volume"] ?: obj["volume"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
                signal = (dataObj["signal"] ?: obj["signal"])?.jsonPrimitive?.contentOrNull,
                score = (dataObj["score"] ?: obj["score"])?.jsonPrimitive?.contentOrNull?.toDoubleOrNull(),
            )

            AgentResponse(
                sessionId = dataObj["sessionId"]?.jsonPrimitive?.contentOrNull ?: obj["sessionId"]?.jsonPrimitive?.contentOrNull,
                content = content,
                summary = summary,
                provider = dataObj["provider"]?.jsonPrimitive?.contentOrNull ?: obj["provider"]?.jsonPrimitive?.contentOrNull,
                market = dataObj["market"]?.jsonPrimitive?.contentOrNull ?: obj["market"]?.jsonPrimitive?.contentOrNull,
                symbol = dataObj["symbol"]?.jsonPrimitive?.contentOrNull ?: obj["symbol"]?.jsonPrimitive?.contentOrNull,
                timeframe = dataObj["timeframe"]?.jsonPrimitive?.contentOrNull ?: obj["timeframe"]?.jsonPrimitive?.contentOrNull,
                toolsUsed = emptyList(),
                toolCalls = emptyList(),
                tasks = emptyList(),
                marketData = marketData,
                timestamp = dataObj["timestamp"]?.jsonPrimitive?.contentOrNull ?: obj["timestamp"]?.jsonPrimitive?.contentOrNull,
                signals = signals,
            )
        } catch (e: Exception) {
            AgentResponse(content = json)
        }
    }
}
