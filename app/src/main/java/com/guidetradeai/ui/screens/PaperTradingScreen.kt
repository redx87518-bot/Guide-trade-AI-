package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.navigation.NavHostController

@Composable
fun PaperTradingScreen(navController: NavHostController) {
    val viewModel: PaperTradingViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadDashboard() }
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Paper Trading",
                subtitle = "Simulation only",
                navigationIcon = null,
                actions = {
                    Text(
                        text = "SIMULATED",
                        color = GuideTradeColors.Warning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(GuideTradeColors.WarningSoft, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        when (uiState) {
            is PaperTradingUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
            is PaperTradingUiState.Error -> ErrorState(
                title = "Failed to load paper trading",
                message = (uiState as PaperTradingUiState.Error).message,
                onRetry = { viewModel.loadDashboard() },
                modifier = Modifier.fillMaxSize(),
            is PaperTradingUiState.Success -> {
                val data = (uiState as PaperTradingUiState.Success).data
                val account = data.account
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoCard(
                                title = "Portfolio",
                                value = "$${"%.2f".format(account?.equity ?: 0.0)}",
                                modifier = Modifier.weight(1f),
                                tint = GuideTradeColors.BrightPurple,
                            )
                                title = "Cash",
                                value = "$${"%.2f".format(account?.balance ?: 0.0)}",
                        }
                    }
                                title = "Unrealized P/L",
                                value = "$${"%.2f".format(account?.unrealizedPnl ?: 0.0)}",
                                tint = GuideTradeColors.Positive,
                                title = "Buying Power",
                                value = "$${"%.2f".format(account?.buyingPower ?: 0.0)}",
                    item { Divider(color = GuideTradeColors.SubtleBorder) }
                    if (data.positions.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Positions",
                                modifier = Modifier.padding(vertical = 8.dp),
                        items(data.positions) { position ->
                            GuideTradeCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = position.symbol, color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = "${position.quantity} shares", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "$${"%.2f".format(position.marketValue)}", color = GuideTradeColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        val pnlColor = if (position.unrealizedPnl >= 0) GuideTradeColors.Positive else GuideTradeColors.Negative
                                        Text(text = "${if (position.unrealizedPnl >= 0) "+" else ""}${"%.2f".format(position.unrealizedPnl)}", color = pnlColor, fontSize = 12.sp)
                                }
                            }
                    if (data.orders.isNotEmpty()) {
                        item { Divider(color = GuideTradeColors.SubtleBorder) }
                                title = "Recent Orders",
                        items(data.orders.take(10)) { order ->
                                        Text(text = "${order.side.uppercase()} ${order.symbol}", color = GuideTradeColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = "${order.quantity} @ $${"%.2f".format(order.price)}", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                                    StatusBadge(text = order.status, color = when (order.status.lowercase()) {
                                        "filled" -> GuideTradeColors.Positive
                                        "pending" -> GuideTradeColors.Warning
                                        "cancelled" -> GuideTradeColors.Negative
                                        else -> GuideTradeColors.MutedText
                                    })
                        PrimaryButton(
                            text = "New Order",
                            onClick = { /* TODO open order sheet */ },
                            modifier = Modifier.fillMaxWidth(),
                        )
                }
            }
            is PaperTradingUiState.OrderPlaced -> {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.refresh()
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GuideTradeColors.Positive, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Paper order completed", color = GuideTradeColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            is PaperTradingUiState.OrderFailed -> {
                        Text(text = "Order failed", color = GuideTradeColors.Negative, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.refresh() }) { Text("Retry", color = GuideTradeColors.BrightPurple) }
        }
    }
}