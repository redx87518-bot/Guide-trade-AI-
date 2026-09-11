package com.guidetradeai.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeCard
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AuthUiState
import com.guidetradeai.viewmodel.AuthViewModel

@Composable
fun HomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val authState = authViewModel.uiState.collectAsStateWithLifecycle().value
    val userName = (authState as? AuthUiState.Authenticated)?.userId ?: "Trader"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07070B))
            .padding(20.dp),
    ) {
        Text(
            text = "Welcome back,",
            color = Color(0xFFA89FB2),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = userName,
            color = Color(0xFFF8F7FC),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
                    )
                )
                .padding(20.dp),
        ) {
            Column {
                Text("GuideTrade Agent", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("AI Market Intelligence", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.18f)),
                ) {
                    Text(
                        text = "Ready to assist with research, chat, and market signals.",
                        modifier = Modifier.padding(12.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Quick Actions", color = Color(0xFFF8F7FC), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GuideTradeCard(onClick = { navController.navigate(NavRoutes.Chat.route()) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color(0xFF8B5CF6))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI Chat", color = Color(0xFFF8F7FC), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Ask anything about markets", color = Color(0xFFA89FB2), fontSize = 12.sp)
                    }
                }
            }

            GuideTradeCard(onClick = { navController.navigate(NavRoutes.Markets.route) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFA78BFA).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Default.ShowChart, contentDescription = null, tint = Color(0xFFA78BFA))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Markets", color = Color(0xFFF8F7FC), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Symbols, signals and context", color = Color(0xFFA89FB2), fontSize = 12.sp)
                    }
                }
            }

            GuideTradeCard(onClick = { navController.navigate(NavRoutes.PaperTrading.route) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF22C55E).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(imageVector = Icons.Default.AutoGraph, contentDescription = null, tint = Color(0xFF22C55E))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Paper Trading", color = Color(0xFFF8F7FC), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text("Simulated orders and P&L", color = Color(0xFFA89FB2), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
