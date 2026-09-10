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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController

@Composable
fun ResearchHistoryScreen(navController: NavHostController) {
    val researchViewModel: ResearchViewModel = viewModel()
    val uiState by researchViewModel.uiState.collectAsState()
    val results = (uiState as? ResearchHistoryUiState.Success)?.results ?: emptyList()
    LaunchedEffect(Unit) { researchViewModel.loadResearchHistory() }
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Research",
                navigationIcon = null,
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        if (results.isEmpty()) {
            EmptyState(
                title = "No research yet",
                description = "Start a conversation with GuideTrade Agent to generate research.",
                modifier = Modifier.fillMaxSize().padding(padding),
                action = {
                    PrimaryButton(text = "Open AI Chat", onClick = { navController.navigate(NavRoutes.AGENT) })
                },
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(results) { result ->
                    GuideTradeCard(
                        onClick = { navController.navigate(NavRoutes.researchDetailRoute(result.id)) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = result.title, color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(text = result.createdAt.formatDate("MMM dd, yyyy"), color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                            }
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}