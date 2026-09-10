import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

package com.guidetradeai.ui.screens

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { homeViewModel.loadHome() }
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "GUIDETRADE AI",
                subtitle = "Market intelligence, simplified.",
                navigationIcon = null,
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = GuideTradeColors.TextPrimary)
                    }
                },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeroCard(
                    onAskGuideTrade = {
                        navController.navigate(NavRoutes.AGENT)
                    },
                    onAnalyzeAsset = {
                        navController.navigate(NavRoutes.MARKETS)
                )
            }
                SectionHeader(
                    title = "Market Overview",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InfoCard(
                        title = "Regime",
                        value = "Neutral",
                        modifier = Modifier.weight(1f),
                    )
                        title = "Risk",
                        value = "Moderate",
                        tint = GuideTradeColors.Warning,
                        title = "Status",
                        value = "Active",
                        tint = GuideTradeColors.Positive,
                }
            item { Divider(modifier = Modifier.padding(horizontal = 20.dp), color = GuideTradeColors.SubtleBorder) }
                    title = "Latest Intelligence",
            items(listOf(
                "Latest Signal" to "BTC/USD",
                "Market Risk" to "Moderate",
                "Major Event" to "Fed Rate Decision",
                "Market Regime" to "Neutral",
            )) { (title, value) ->
                GuideTradeCard(
                    onClick = { /* TODO */ },
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                color = GuideTradeColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                                text = "Just now",
                                color = GuideTradeColors.MutedText,
                                fontSize = 11.sp,
                        }
                        Text(
                            text = value,
                            color = GuideTradeColors.BrightPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                        )
                    title = "Watchlist",
                    action = {
                        TextButton(onClick = { /* TODO */ }) {
                            Text(text = "Add", color = GuideTradeColors.BrightPurple, fontSize = 12.sp)
                Triple("BTC", "Bitcoin", 67421.32 to 2.34),
                Triple("ETH", "Ethereum", 3456.78 to -1.23),
                Triple("AAPL", "Apple Inc.", 189.55 to 0.87),
            )) { (symbol, name, data) ->
                val parts = data.toString().split(" to ")
                val price = parts[0].toDoubleOrNull()
                val change = parts.getOrNull(1)?.toDoubleOrNull()
                AssetRow(
                    symbol = symbol,
                    name = name,
                    price = price,
                    changePercent = change,
                    onClick = { navController.navigate(NavRoutes.assetDetailRoute(symbol)) },
        }
    }
}
fun HeroCard(
    onAskGuideTrade: () -> Unit,
    onAnalyzeAsset: () -> Unit,
    GuideTradeCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        containerColor = Color.Transparent,
    ) {
        Box(
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GuideTradeColors.PrimaryPurple.copy(alpha = 0.25f),
                            GuideTradeColors.PrimarySurface.copy(alpha = 0.9f),
                            GuideTradeColors.PrimarySurface,
                        ),
                        center = Alignment.TopCenter,
                        radius = 500f,
                    ),
                    shape = RoundedCornerShape(20.dp),
                .border(1.dp, GuideTradeColors.SubtleBorder, RoundedCornerShape(20.dp)),
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Good evening.",
                        color = GuideTradeColors.TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                    Spacer(modifier = Modifier.height(4.dp))
                        text = "What's happening in the markets?",
                        color = GuideTradeColors.TextSecondary,
                        fontSize = 15.sp,
                    PrimaryButton(
                        text = "Ask GuideTrade",
                        onClick = onAskGuideTrade,
                        icon = Icons.Default.Sparkles,
                    SecondaryButton(
                        text = "Analyze an asset",
                        onClick = onAnalyzeAsset,
                        icon = Icons.Default.Analytics,