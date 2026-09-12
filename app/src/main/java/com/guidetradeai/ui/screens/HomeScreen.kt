package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.SignalCard
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.SignalsViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: SignalsViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val signals = if (uiState is com.guidetradeai.viewmodel.SignalsUiState.Success) uiState.signals else emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "GuideTrade AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Market Intelligence",
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = { navController.navigate(NavRoutes.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Market Overview",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val markets = listOf("Crypto", "Forex", "Stocks", "Commodities")
                items(markets) { market ->
                    Card(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp),
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = market.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (market == "Crypto") "BTC • Active" else if (market == "Forex") "EUR/USD" else if (market == "Stocks") "Market open" else "Gold",
                                fontSize = 11.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Top Signals",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                androidx.compose.material3.TextButton(onClick = { navController.navigate(NavRoutes.Signals.route) }) {
                    Text("View all", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (signals.isNotEmpty()) {
            items(signals.take(3)) { signal ->
                SignalCard(
                    signal = signal,
                    onClick = { navController.navigate(NavRoutes.SignalDetails.route(signal.id)) },
                )
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "No signals available right now.",
                        fontSize = 13.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Watchlist",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                androidx.compose.material3.TextButton(onClick = { navController.navigate(NavRoutes.Watchlist.route) }) {
                    Text("View all", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                ),
                onClick = { navController.navigate(NavRoutes.Signals.route) },
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "BTC/USD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Crypto • 1H",
                                fontSize = 12.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        androidx.compose.material3.Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        ) {
                            Text(
                                text = "BULLISH",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Quick Actions",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { navController.navigate(NavRoutes.Analyze.route) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Analyze")
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = { navController.navigate(NavRoutes.Agent.route) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Ask Agent")
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = { navController.navigate(NavRoutes.PaperTrading.route) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Paper Test")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
