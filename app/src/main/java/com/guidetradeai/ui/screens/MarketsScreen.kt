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
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.navigation.NavHostController

@Composable
fun MarketsScreen(navController: NavHostController) {
    var selectedMarket by rememberSaveable { mutableStateOf("All") }
    val markets = listOf("All", "Stocks", "Crypto", "Forex", "Commodities")
    val viewModel: MarketsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(selectedMarket) {
        if (selectedMarket == "All") viewModel.loadSymbols("crypto")
        else viewModel.loadSymbols(selectedMarket.lowercase())
    }
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Markets",
                navigationIcon = null,
                actions = {
                    IconButton(onClick = { /* TODO search */ }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = GuideTradeColors.TextPrimary)
                    }
                },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                markets.forEach { market ->
                    val isSelected = selectedMarket == market
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) GuideTradeColors.PrimaryPurple else GuideTradeColors.SecondarySurface,
                                RoundedCornerShape(10.dp),
                            )
                            .clickable { selectedMarket = market }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = market,
                            color = if (isSelected) GuideTradeColors.White else GuideTradeColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (uiState) {
                is MarketsUiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
                is MarketsUiState.Error -> ErrorState(
                    title = "Failed to load markets",
                    message = (uiState as MarketsUiState.Error).message,
                    onRetry = { viewModel.loadSymbols(selectedMarket.lowercase()) },
                    modifier = Modifier.fillMaxSize(),
                )
                is MarketsUiState.Success -> {
                    val symbols = (uiState as MarketsUiState.Success).symbols
                    if (symbols.isEmpty()) {
                        EmptyState(
                            title = "No symbols found",
                            description = "Try selecting a different market category.",
                            modifier = Modifier.fillMaxSize(),
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(symbols) { symbol ->
                                GuideTradeCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { navController.navigate(NavRoutes.assetDetailRoute(symbol.symbol)) },
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(GuideTradeColors.PrimarySurface, CircleShape),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text = symbol.symbol.take(2).uppercase(),
                                                    color = GuideTradeColors.BrightPurple,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(text = symbol.symbol, color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                                Text(text = symbol.name.ifBlank { symbol.market }, color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = GuideTradeColors.MutedText,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    }
                                }
                            }
                        }
        }
}