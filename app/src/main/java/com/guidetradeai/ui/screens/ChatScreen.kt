package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    navController: NavHostController,
    sessionId: String?,
) {
    var message by remember { mutableStateOf("") }
    val viewModel: ChatViewModel = ChatViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Chat", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = "Send",
            onClick = {
                if (message.isNotBlank()) {
                    viewModel.sendMessage(sessionId ?: java.util.UUID.randomUUID().toString(), message)
                    message = ""
                }
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            text = "Back",
            onClick = { navController.popBackStack() },
        )
    }
}
