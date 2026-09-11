package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import com.guidetradeai.domain.model.AgentSession
import com.guidetradeai.domain.model.PaperAccount
import com.guidetradeai.domain.model.PaperOrder
import com.guidetradeai.domain.model.PaperOrderRequest
import com.guidetradeai.domain.model.PaperPosition
import com.guidetradeai.domain.model.PaperTrade
import com.guidetradeai.domain.model.SymbolItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

class PaperTradingRepository(
    private val supabase: SupabaseClient,
) {
    private val json = Json { ignoreUnknownKeys = true }

    private fun currentUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: ""
    }

    suspend fun getDashboard(): Result<PaperDashboardData> {
        return try {
            val userId = currentUserId()
            val accountResult = supabase.postgrest.from("paper_accounts").select {
                filter { eq("user_id", userId) }
            }.decodeList<JsonObject>()
            val account = accountResult.firstOrNull()?.let { mapToPaperAccount(it) }
            val positions = supabase.postgrest.from("paper_positions").select {
                filter { eq("user_id", userId) }
            }.decodeList<JsonObject>().map { mapToPaperPosition(it) }
            val orders = supabase.postgrest.from("paper_orders").select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
                limit(20)
            }.decodeList<JsonObject>().map { mapToPaperOrder(it) }
            val trades = supabase.postgrest.from("paper_trades").select {
                filter { eq("user_id", userId) }
                order("created_at", Order.DESCENDING)
                limit(50)
            }.decodeList<JsonObject>().map { mapToPaperTrade(it) }
            Result.Success(PaperDashboardData(account, positions, orders, trades))
        } catch (e: Exception) {
            Result.Error("Failed to load paper trading dashboard: ${e.message}")
        }
    }

    suspend fun placeOrder(request: PaperOrderRequest): Result<PaperOrder> {
        return try {
            val body = buildJsonObject {
                put("symbol", JsonPrimitive(request.symbol))
                put("side", JsonPrimitive(request.side))
                put("quantity", JsonPrimitive(request.quantity))
                put("order_type", JsonPrimitive(request.orderType))
                put("limit_price", request.limitPrice?.let { JsonPrimitive(it) } ?: JsonPrimitive(null))
            }
            val userId = currentUserId()
            val newOrder = buildJsonObject {
                put("user_id", JsonPrimitive(userId))
                put("symbol", JsonPrimitive(request.symbol))
                put("side", JsonPrimitive(request.side))
                put("quantity", JsonPrimitive(request.quantity))
                put("order_type", JsonPrimitive(request.orderType))
                put("limit_price", JsonPrimitive(request.limitPrice))
                put("price", JsonPrimitive(request.limitPrice ?: 0.0))
                put("notional_value", JsonPrimitive((request.limitPrice ?: 0.0) * request.quantity))
                put("status", JsonPrimitive("pending"))
            }
            supabase.postgrest.from("paper_orders").insert(newOrder)
            
            // Update position or create new one
            val existingPositions = supabase.postgrest.from("paper_positions")
                .select { filter { eq("user_id", userId); eq("symbol", request.symbol) } }
                .decodeList<JsonObject>()
            
            if (existingPositions.isNotEmpty()) {
                val existing = existingPositions.first()
                val currentQty = existing["quantity"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                val currentAvg = existing["avg_entry"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                val newQty = currentQty + request.quantity
                val newAvg = if (newQty > 0) (currentAvg * currentQty + (request.limitPrice ?: 0.0) * request.quantity) / newQty else 0.0
                
                supabase.postgrest.from("paper_positions").update(buildJsonObject {
                    put("quantity", JsonPrimitive(newQty))
                    put("avg_entry", JsonPrimitive(newAvg))
                    put("current_price", JsonPrimitive(request.limitPrice ?: 0.0))
                    put("market_value", JsonPrimitive(newQty * (request.limitPrice ?: 0.0)))
                }) {
                    filter { eq("id", existing["id"]?.jsonPrimitive?.content ?: "") }
                }
            } else {
                supabase.postgrest.from("paper_positions").insert(buildJsonObject {
                    put("user_id", JsonPrimitive(userId))
                    put("symbol", JsonPrimitive(request.symbol))
                    put("quantity", JsonPrimitive(request.quantity))
                    put("avg_entry", JsonPrimitive(request.limitPrice ?: 0.0))
                    put("current_price", JsonPrimitive(request.limitPrice ?: 0.0))
                    put("market_value", JsonPrimitive(request.quantity * (request.limitPrice ?: 0.0)))
                    put("unrealized_pnl", JsonPrimitive(0.0))
                    put("unrealized_pnl_percent", JsonPrimitive(0.0))
                })
            }
            
            // Create trade record
            supabase.postgrest.from("paper_trades").insert(buildJsonObject {
                put("user_id", JsonPrimitive(userId))
                put("symbol", JsonPrimitive(request.symbol))
                put("side", JsonPrimitive(request.side))
                put("quantity", JsonPrimitive(request.quantity))
                put("price", JsonPrimitive(request.limitPrice ?: 0.0))
                put("notional_value", JsonPrimitive((request.limitPrice ?: 0.0) * request.quantity))
            })
            
            Result.Success(PaperOrder(
                id = java.util.UUID.randomUUID().toString(),
                userId = userId,
                symbol = request.symbol,
                side = request.side,
                quantity = request.quantity,
                price = request.limitPrice ?: 0.0,
                notionalValue = (request.limitPrice ?: 0.0) * request.quantity,
                orderType = request.orderType,
                status = "pending",
                createdAt = java.time.Instant.now().toString()
            ))
        } catch (e: Exception) {
            Result.Error("Failed to place paper order: ${e.message}")
        }
    }

    private fun mapPaperError(error: String): String = when (error) {
        "AUTH_REQUIRED" -> "Authentication required."
        "INSUFFICIENT_FUNDS" -> "Insufficient paper trading funds."
        "INVALID_ORDER" -> "Invalid order parameters."
        "REAL_TRADING_DISABLED" -> "Live trading is disabled."
        "ORDER_FAILED" -> "The simulated order could not be completed."
        else -> error
    }
}