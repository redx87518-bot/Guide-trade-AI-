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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.navigation.NavRoutes

@Composable
fun SettingsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Settings", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.TelegramSettings.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Telegram Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.VoiceSettings.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Voice Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.Profile.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Profile")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.PaperTrading.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Paper Trading")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate(NavRoutes.McpConnections.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("MCP Connections")
        }
    }
}
