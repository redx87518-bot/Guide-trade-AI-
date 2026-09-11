package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(navController: NavHostController) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Settings", fontSize = androidx.compose.ui.unit.sp(28))
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("telegram_settings") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("Telegram Settings")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("voice_settings") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("Voice Settings")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("profile") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("Profile")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.PaperTrading.route) },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("Paper Trading")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.McpConnections.route) },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("MCP Connections")
        }
    }
}
