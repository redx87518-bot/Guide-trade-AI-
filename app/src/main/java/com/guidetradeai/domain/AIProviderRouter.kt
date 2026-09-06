package com.guidetradeai.domain

import com.guidetradeai.domain.model.SymbolItem
import com.guidetradeai.domain.Result

sealed interface AIProviderRouter {
    suspend fun sendMessage(sessionId: String, message: String): Result<String>
    suspend fun listSymbols(market: String): Result<List<SymbolItem>>
}
