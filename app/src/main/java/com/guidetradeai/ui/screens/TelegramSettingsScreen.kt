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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun TelegramSettingsScreen(navController: NavHostController) {
    var botToken by remember { mutableStateOf("") }
    var chatId by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(false) }

    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Telegram Settings", fontSize = androidx.compose.ui.unit.sp(24))
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        TextField(
            value = botToken,
            onValueChange = { botToken = it },
            label = { Text("Bot Token") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        )
        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        TextField(
            value = chatId,
            onValueChange = { chatId = it },
            label = { Text("Chat ID") },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        )
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        Button(
            onClick = { navController.popBackStack() },
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        ) {
            Text("Save")
        }
    }
}
