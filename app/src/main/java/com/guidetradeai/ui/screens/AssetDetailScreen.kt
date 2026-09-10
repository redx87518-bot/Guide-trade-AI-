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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController

@Composable
fun AssetDetailScreen(
    symbol: String,
    navController: NavHostController,
    chatViewModel: ChatViewModel = viewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Chart", "Signals", "Analysis", "Risk", "Events")
    val chatVm: ChatViewModel = viewModel()
    LaunchedEffect(symbol) {
        chatVm.setSymbol(symbol)
    }
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = symbol.uppercase(),
                subtitle = "Asset Details",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = {
                        chatViewModel.sendMessage("Analyze $symbol")
                        navController.navigate(NavRoutes.AGENT)
                    }) {
                        Icon(imageVector = Icons.Default.Sparkles, contentDescription = "Analyze", tint = GuideTradeColors.TextPrimary)
                    }
                },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GuideTradeColors.PrimarySurface,
                contentColor = GuideTradeColors.BrightPurple,
                indicatorColor = GuideTradeColors.PrimaryPurple,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal) },
                    )
                }
            }
            when (selectedTab) {
                0 -> OverviewTab(symbol = symbol)
                1 -> ChartTab(symbol = symbol)
                2 -> SignalsTab(symbol = symbol)
                3 -> AnalysisTab(symbol = symbol)
                4 -> RiskTab(symbol = symbol)
                5 -> EventsTab(symbol = symbol)
        }
}
fun OverviewTab(symbol: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(text = symbol.uppercase(), color = GuideTradeColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(text = "Loading price data...", color = GuideTradeColors.TextSecondary, fontSize = 14.sp)
            GuideTradeCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(title = "Regime", value = "Neutral", modifier = Modifier.weight(1f))
                        InfoCard(title = "Risk", value = "Moderate", modifier = Modifier.weight(1f), tint = GuideTradeColors.Warning)
            PrimaryButton(
                text = "Analyze with GuideTrade Agent",
                onClick = { /* handled by top bar */ },
                icon = Icons.Default.Sparkles,
fun ChartTab(symbol: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Chart data for $symbol", color = GuideTradeColors.TextSecondary)
fun SignalsTab(symbol: String) {
        Text(text = "Signals for $symbol", color = GuideTradeColors.TextSecondary)
fun AnalysisTab(symbol: String) {
        Text(text = "Analysis for $symbol", color = GuideTradeColors.TextSecondary)
fun RiskTab(symbol: String) {
        Text(text = "Risk assessment for $symbol", color = GuideTradeColors.TextSecondary)
fun EventsTab(symbol: String) {
        Text(text = "Events for $symbol", color = GuideTradeColors.TextSecondary)