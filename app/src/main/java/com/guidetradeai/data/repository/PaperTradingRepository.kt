package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.data.remote.PaperOrderData
import com.guidetradeai.data.remote.PaperPositionData
import com.guidetradeai.data.remote.PaperTradeData
import com.guidetradeai.domain.Result

class PaperTradingRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getPositions(): Result<List<PaperPositionData>> {
        return supabase.getPaperPositions()
    }

    suspend fun getOrders(): Result<List<PaperOrderData>> {
        return supabase.getPaperOrders()
    }

    suspend fun placeOrder(order: PaperOrderData): Result<PaperOrderData> {
        return supabase.placePaperOrder(order)
    }

    suspend fun getTrades(): Result<List<PaperTradeData>> {
        return supabase.getPaperTrades()
    }
}
