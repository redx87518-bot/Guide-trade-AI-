package com.guidetradeai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Signal(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    val market: String = "crypto",
    val timeframe: String = "1H",
    val direction: String = "neutral",
    val strength: Double = 0.0,
    val entry: Double? = null,
    val invalidation: Double? = null,
    val target1: Double? = null,
    val target2: Double? = null,
    val target3: Double? = null,
    val currentPrice: Double? = null,
    val summary: String? = null,
    val analysis: String? = null,
    val risk: String? = null,
    val marketContext: String? = null,
    val technicalInfo: String? = null,
    val why: String? = null,
    val source: String? = "NORTH7",
    val timestamp: String = "",
    val updatedAt: String = "",
)

@Serializable
data class WatchlistItem(
    val id: String = "",
    val userId: String = "",
    val symbol: String = "",
    val market: String = "crypto",
    val timeframe: String = "1H",
    val addedAt: String = "",
)

@Serializable
data class SignalFilter(
    val market: String? = null,
    val timeframe: String? = null,
    val direction: String? = null,
    val search: String = "",
)

@Serializable
data class AnalysisRequest(
    val market: String,
    val symbol: String,
    val timeframe: String,
    val analysisType: String = "signal",
)

@Serializable
data class AnalysisResult(
    val signal: Signal? = null,
    val content: String? = null,
    val summary: String? = null,
)
