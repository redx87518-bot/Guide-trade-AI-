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
fun McpConnectionsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "MCP Connections",
                subtitle = "Advanced integrations",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navController.popBackStack() },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                EmptyState(
                    title = "No connections yet",
                    description = "Connect an MCP provider to enable advanced trading integrations.",
                    action = {
                        PrimaryButton(text = "Add Connection", onClick = { /* TODO */ })
                    },
                )
            }
                GuideTradeCard {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Live Trading", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Not available in GuideTrade AI", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        StatusBadge(text = "DISABLED", color = GuideTradeColors.Negative)
                    }
                }
                            Text(text = "Paper Trading", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Simulated trading only", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        StatusBadge(text = "READ ONLY", color = GuideTradeColors.Positive)
        }
    }
}