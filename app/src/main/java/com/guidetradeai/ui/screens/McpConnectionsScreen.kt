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
fun McpConnectionsScreen(navController: NavHostController) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("MCP Connections", fontSize = androidx.compose.ui.unit.sp(28))
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("Add Connection")
        }
    }
}
