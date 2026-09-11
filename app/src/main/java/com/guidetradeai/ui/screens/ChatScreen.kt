package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guidetradeai.viewModel.ChatViewModel
import com.guidetradeai.domain.model.ChatMessage

@Composable
fun ChatScreen(
    navController: NavHostController,
    sessionId: String? = null,
    viewModel: ChatViewModel = ChatViewModel(),
) {
    var message by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            messages.forEach { msg: ChatMessage ->
                Text(
                    text = "${msg.role}: ${msg.content}",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Column {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type a message") },
            )
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        viewModel.sendMessage(sessionId ?: "", message)
                        message = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Send")
            }
        }
    }
}