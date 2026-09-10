package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.PaperAccount
import com.guidetradeai.domain.model.PaperDashboardData
import com.guidetradeai.domain.model.PaperOrder
import com.guidetradeai.domain.model.PaperOrderRequest
import com.guidetradeai.domain.model.PaperPosition
import com.guidetradeai.domain.model.PaperTrade
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class PaperTradingRepository(
    private val supabase: SupabaseClient,
) {
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
            Result.success(PaperDashboardData(account, positions, orders, trades))
        } catch (e: Exception) {
            Result.error("Failed to load paper trading dashboard: ${e.message}")
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
            val response = supabase.functions.invoke("paper-order", body = body)
            val data = response.bodyAsText()
            val jsonObject = Json.parseToJsonElement(data).jsonObject
            val error = jsonObject["error"]?.jsonPrimitive?.content
            if (error != null) {
                return Result.error(mapPaperError(error))
            }
            val orderObj = jsonObject["order"]?.jsonObject ?: jsonObject
            Result.success(mapToPaperOrder(orderObj))
        } catch (e: Exception) {
            Result.error("Failed to place paper order: ${e.message}")
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

    private fun mapToPaperAccount(json: JsonObject): PaperAccount {
        return PaperAccount(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            userId = json["user_id"]?.jsonPrimitive?.content ?: "",
            balance = json["balance"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            equity = json["equity"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            buyingPower = json["buying_power"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            createdAt = json["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = json["updated_at"]?.jsonPrimitive?.content ?: "",
        )
    }

    private fun mapToPaperPosition(json: JsonObject): PaperPosition {
        return PaperPosition(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            userId = json["user_id"]?.jsonPrimitive?.content ?: "",
            symbol = json["symbol"]?.jsonPrimitive?.content ?: "",
            quantity = json["quantity"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            avgEntry = json["avg_entry"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            currentPrice = json["current_price"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            marketValue = json["market_value"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            unrealizedPnl = json["unrealized_pnl"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            unrealizedPnlPercent = json["unrealized_pnl_percent"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            createdAt = json["created_at"]?.jsonPrimitive?.content ?: "",
            updatedAt = json["updated_at"]?.jsonPrimitive?.content ?: "",
        )
    }

    private fun mapToPaperOrder(json: JsonObject): PaperOrder {
        return PaperOrder(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            userId = json["user_id"]?.jsonPrimitive?.content ?: "",
            symbol = json["symbol"]?.jsonPrimitive?.content ?: "",
            side = json["side"]?.jsonPrimitive?.content ?: "",
            quantity = json["quantity"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            price = json["price"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            notionalValue = json["notional_value"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            orderType = json["order_type"]?.jsonPrimitive?.content ?: "",
            status = json["status"]?.jsonPrimitive?.content ?: "",
            createdAt = json["created_at"]?.jsonPrimitive?.content ?: "",
        )
    }

    private fun mapToPaperTrade(json: JsonObject): PaperTrade {
        return PaperTrade(
            id = json["id"]?.jsonPrimitive?.content ?: "",
            orderId = json["order_id"]?.jsonPrimitive?.content ?: "",
            userId = json["user_id"]?.jsonPrimitive?.content ?: "",
            symbol = json["symbol"]?.jsonPrimitive?.content ?: "",
            side = json["side"]?.jsonPrimitive?.content ?: "",
            quantity = json["quantity"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            price = json["price"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            notionalValue = json["notional_value"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            createdAt = json["created_at"]?.jsonPrimitive?.content ?: "",
        )
    }
}
