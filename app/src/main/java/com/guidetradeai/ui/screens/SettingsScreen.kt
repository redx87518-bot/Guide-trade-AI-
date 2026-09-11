package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeCard
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.viewmodel.AuthViewModel

@Composable
fun SettingsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Account and preferences", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(20.dp))

        GuideTradeCard(onClick = { navController.navigate("profile") }) {
            Text("Profile", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeCard(onClick = { navController.navigate("telegram_settings") }) {
            Text("Telegram", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeCard(onClick = { navController.navigate("voice_settings") }) {
            Text("Voice", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeCard(onClick = { navController.navigate("paper_trading") }) {
            Text("Paper Trading", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeCard(onClick = { navController.navigate("mcp_connections") }) {
            Text("MCP Connections", fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            text = "Sign Out",
            onClick = {
                authViewModel.signOut()
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            },
        )
    }
}
