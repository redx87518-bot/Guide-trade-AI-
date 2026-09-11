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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeTextField
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.ui.components.SecondaryButton

@Composable
fun TelegramSettingsScreen(navController: NavHostController) {
    var botToken by remember { mutableStateOf("") }
    var chatId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Telegram Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Connect your Telegram bot", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(20.dp))
        GuideTradeTextField(
            value = botToken,
            onValueChange = { botToken = it },
            label = "Bot Token",
        )
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeTextField(
            value = chatId,
            onValueChange = { chatId = it },
            label = "Chat ID",
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "Save",
            onClick = { navController.popBackStack() },
        )
        Spacer(modifier = Modifier.height(8.dp))
        SecondaryButton(
            text = "Back",
            onClick = { navController.popBackStack() },
        )
    }
}
