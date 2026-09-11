package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.data.remote.PaperOrderData
import com.guidetradeai.data.remote.PaperPositionData
import com.guidetradeai.data.remote.PaperTradeData
import com.guidetradeai.domain.Result

class PaperTradingRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun getPositions(): Result<List<PaperPositionData>> {
        return try {
            val result = supabase.postgrest.from("paper_positions").select {}.decodeList<PaperPositionData>()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load positions")
        }
    }

    suspend fun getOrders(): Result<List<PaperOrderData>> {
        return try {
            val result = supabase.postgrest.from("paper_orders").select {}.decodeList<PaperOrderData>()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load orders")
        }
    }

    suspend fun placeOrder(order: PaperOrderData): Result<PaperOrderData> {
        return try {
            supabase.postgrest.from("paper_orders").insert(order)
            Result.Success(order)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to place order")
        }
    }

    suspend fun getTrades(): Result<List<PaperTradeData>> {
        return try {
            val result = supabase.postgrest.from("paper_trades").select {}.decodeList<PaperTradeData>()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load trades")
        }
    }
}
