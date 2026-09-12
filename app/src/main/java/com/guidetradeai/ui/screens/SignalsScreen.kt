package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.EmptyState
import com.guidetradeai.ui.components.ErrorState
import com.guidetradeai.ui.components.LoadingState
import com.guidetradeai.ui.components.SignalCard
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.SignalsViewModel

@Composable
fun SignalsScreen(navController: NavHostController, viewModel: SignalsViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val filter = viewModel.filter.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Signals",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val markets = listOf("All", "Crypto", "Forex", "Stocks", "Commodities")
            items(markets) { market ->
                FilterChip(
                    selected = filter.market.equals(market, ignoreCase = true) || (market == "All" && filter.market == null),
                    onClick = {
                        val newMarket = if (market == "All") null else market
                        viewModel.updateFilter(filter.copy(market = newMarket))
                    },
                    label = { Text(market, fontSize = 12.sp) },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Timeframe",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            val timeframes = listOf("All", "1m", "5m", "15m", "1H", "4H", "1D")
            items(timeframes) { tf ->
                FilterChip(
                    selected = filter.timeframe.equals(tf, ignoreCase = true) || (tf == "All" && filter.timeframe == null),
                    onClick = {
                        val newTf = if (tf == "All") null else tf
                        viewModel.updateFilter(filter.copy(timeframe = newTf))
                    },
                    label = { Text(tf, fontSize = 12.sp) },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val directions = listOf("All", "Bullish", "Bearish", "Neutral")
            items(directions) { dir ->
                FilterChip(
                    selected = filter.direction.equals(dir, ignoreCase = true) || (dir == "All" && filter.direction == null),
                    onClick = {
                        val newDir = if (dir == "All") null else dir
                        viewModel.updateFilter(filter.copy(direction = newDir))
                    },
                    label = { Text(dir, fontSize = 12.sp) },
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (uiState) {
            is com.guidetradeai.viewmodel.SignalsUiState.Loading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(3) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
            is com.guidetradeai.viewmodel.SignalsUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.SignalsUiState.Empty -> {
                EmptyState(
                    title = "No signals found",
                    description = "Try another market or timeframe.",
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.SignalsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(uiState.signals) { signal ->
                        SignalCard(
                            signal = signal,
                            onClick = { navController.navigate(NavRoutes.SignalDetails.route(signal.id)) },
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            else -> {}
        }
    }
}
