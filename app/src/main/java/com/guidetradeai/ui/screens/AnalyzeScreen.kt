package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.domain.model.AnalysisRequest
import com.guidetradeai.ui.components.EmptyState
import com.guidetradeai.ui.components.ErrorState
import com.guidetradeai.ui.components.LoadingState
import com.guidetradeai.ui.components.SignalCard
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AnalyzeViewModel

@Composable
fun AnalyzeScreen(navController: NavHostController, viewModel: AnalyzeViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val selectedMarketState = remember { mutableStateOf("crypto") }
    val selectedSymbolState = remember { mutableStateOf("BTC/USD") }
    val selectedTimeframeState = remember { mutableStateOf("1H") }
    val selectedAnalysisState = remember { mutableStateOf("signal") }
    val symbolQueryState = remember { mutableStateOf("") }

    val selectedMarket get() = selectedMarketState.value
    val selectedSymbol get() = selectedSymbolState.value
    val selectedTimeframe get() = selectedTimeframeState.value
    val selectedAnalysis get() = selectedAnalysisState.value
    val symbolQuery get() = symbolQueryState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Analyze Market",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )

        when (uiState) {
            is com.guidetradeai.viewmodel.AnalyzeUiState.Loading -> {
                LoadingState(modifier = Modifier.fillMaxSize())
            }
            is com.guidetradeai.viewmodel.AnalyzeUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.reset() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.AnalyzeUiState.Success -> {
                if (uiState.result.signal != null) {
                    SignalCard(
                        signal = uiState.result.signal,
                        onClick = {
                            navController.navigate(NavRoutes.SignalDetails.route(uiState.result.signal!!.id))
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                uiState.result.content?.let { content ->
                    Text(
                        text = content,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                    )
                }
            }
            else -> {}
        }

        Text("Market", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        val markets = listOf("crypto", "forex", "stocks", "commodities")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(markets) { market ->
                FilterChip(
                    selected = selectedMarket == market,
                    onClick = { selectedMarketState.value = market },
                    label = { Text(market.replaceFirstChar { it.uppercase() }, fontSize = 12.sp) },
                )
            }
        }

        Text("Asset", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        OutlinedTextField(
            value = symbolQuery.ifBlank { selectedSymbol },
            onValueChange = { symbolQueryState.value = it },
            label = { Text("Search symbol") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("BTC/USD", "ETH/USD", "EUR/USD", "AAPL", "XAU/USD").forEach { asset ->
                TextButton(onClick = { selectedSymbolState.value = asset; symbolQuery = asset }) {
                    Text(asset, fontSize = 11.sp)
                }
            }
        }

        Text("Timeframe", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        val timeframes = listOf("1m", "5m", "15m", "1h", "4h", "1D")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(timeframes) { tf ->
                FilterChip(
                    selected = selectedTimeframe.equals(tf, ignoreCase = true),
                    onClick = { selectedTimeframe = tf },
                    label = { Text(tf, fontSize = 12.sp) },
                )
            }
        }

        Text("Analysis", fontWeight = FontWeight.Medium, fontSize = 13.sp)
        val types = listOf("Signal", "Technical", "Market", "Risk", "Full")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(types) { type ->
                FilterChip(
                    selected = selectedAnalysis.equals(type, ignoreCase = true),
                    onClick = { selectedAnalysisState.value = type },
                    label = { Text(type, fontSize = 12.sp) },
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                val symbol = if (symbolQuery.isNotBlank()) symbolQuery else selectedSymbol
                viewModel.analyze(
                    AnalysisRequest(
                        market = selectedMarket,
                        symbol = symbol,
                        timeframe = selectedTimeframe,
                        analysisType = selectedAnalysis,
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Analyze Market")
        }
    }
}
