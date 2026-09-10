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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(navController: NavHostController) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val uiState by settingsViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Settings",
                navigationIcon = null,
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                SectionHeader(title = "Account")
            }
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.PROFILE) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GuideTradeColors.BrightPurple, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Profile", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                Text(text = "Manage your account", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            item { Divider(color = GuideTradeColors.SubtleBorder) }
                SectionHeader(title = "Appearance")
                var selectedTheme by rememberSaveable { mutableStateOf("dark") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Light", "Dark", "System").forEach { theme ->
                        val isSelected = selectedTheme == theme.lowercase()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSelected) GuideTradeColors.PrimaryPurple else GuideTradeColors.SecondarySurface, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedTheme = theme.lowercase()
                                    settingsViewModel.updateTheme(theme.lowercase())
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = theme, color = if (isSelected) GuideTradeColors.White else GuideTradeColors.TextSecondary, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                SectionHeader(title = "AI")
                GuideTradeCard(onClick = { /* TODO */ }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "GuideTrade Agent", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Primary intelligence experience", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        StatusBadge(text = "ACTIVE", color = GuideTradeColors.Positive)
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.VOICE_SETTINGS) }) {
                            Text(text = "Voice", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Voice responses and input", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                SectionHeader(title = "Connections")
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.TELEGRAM_SETTINGS) }) {
                            Text(text = "Telegram", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Connect Telegram bot", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.MCP_CONNECTIONS) }) {
                            Text(text = "MCP Connections", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Advanced integrations", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.ABOUT) }) {
                            Text(text = "About", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Version, privacy, terms", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
        }
    }
}