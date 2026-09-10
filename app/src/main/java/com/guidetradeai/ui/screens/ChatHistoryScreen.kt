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
fun ChatHistoryScreen(navController: NavHostController) {
    val chatHistoryViewModel: ChatHistoryViewModel = viewModel()
    val uiState by chatHistoryViewModel.uiState.collectAsState()
    val sessions = (uiState as? ChatHistoryUiState.Success)?.sessions ?: emptyList()
    LaunchedEffect(Unit) { chatHistoryViewModel.loadSessions() }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    chatHistoryViewModel.createNewSession { sessionId ->
                        navController.navigate(NavRoutes.chatRoute(sessionId))
                    }
                },
                containerColor = GuideTradeColors.PrimaryPurple,
                contentColor = GuideTradeColors.White,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Chat")
            }
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Chat History",
                style = MaterialTheme.typography.headlineMedium,
                color = GuideTradeColors.TextPrimary,
                modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 8.dp),
                fontWeight = FontWeight.W600,
            )
            if (sessions.isEmpty()) {
                EmptyState(
                    title = "No chat sessions yet.",
                    description = "Start a new conversation with GuideTrade Agent.",
                    modifier = Modifier.fillMaxSize(),
                    action = {
                        PrimaryButton(
                            text = "New Chat",
                            onClick = {
                                chatHistoryViewModel.createNewSession { sessionId ->
                                    navController.navigate(NavRoutes.chatRoute(sessionId))
                                }
                            },
                        )
                    },
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(sessions, key = { it.id }) { session ->
                        GuideTradeCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .clickable { navController.navigate(NavRoutes.chatRoute(session.id)) },
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = session.title, color = GuideTradeColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = session.updatedAt.formatDate("MMM dd"), color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(onClick = {
                                        chatHistoryViewModel.deleteSession(session.id)
                                    }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = GuideTradeColors.Negative, modifier = Modifier.size(18.dp))
                                    }
                            }
                        }
                }
        }
    }
}