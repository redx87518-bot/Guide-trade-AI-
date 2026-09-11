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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "GuideTrade",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "AI Market Intelligence",
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = { navController.navigate(NavRoutes.Profile.route) }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    text = "Market Overview",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val markets = listOf("Crypto", "Forex", "Stocks", "Commodities")
                    items(markets) { market ->
                        Card(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp),
                        ) {
                            Text(
                                text = market,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }

            item {
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
                        fontSize = 16.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    )
                    androidx.compose.material3.TextButton(onClick = { navController.navigate(NavRoutes.Signals.route) }) {
                        Text("View all")
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
                    com.guidetradeai.ui.components.EmptyState(
                        title = "No signals yet",
                        description = "Check back soon for market intelligence.",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
