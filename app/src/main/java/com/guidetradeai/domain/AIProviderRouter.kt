package com.guidetradeai.domain

sealed interface AIProviderRouter {
    suspend fun sendMessage(sessionId: String, message: String): Result<String>
    suspend fun listSymbols(market: String): Result<List<SymbolItem>>
}
