package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.guidetradeai.ui.components.AgentProgressIndicator
import com.guidetradeai.ui.components.LoadingState
import com.guidetradeai.viewmodel.AgentViewModel

@Composable
fun AgentScreen(navController: NavHostController, viewModel: AgentViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    var query by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Agent",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )

        when (uiState) {
            is com.guidetradeai.viewmodel.AgentUiState.Loading -> {
                AgentProgressIndicator(
                    steps = listOf("Analyzing request", "Retrieving market context", "Retrieving signal data", "Building analysis"),
                    currentStep = 1,
                    modifier = Modifier.fillMaxWidth(),
                )
                LoadingState(message = "Agent is processing...", modifier = Modifier.fillMaxSize())
            }
            is com.guidetradeai.viewmodel.AgentUiState.Success -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Text(
                            text = uiState.response.content ?: uiState.response.summary ?: "Analysis complete",
                            modifier = Modifier.padding(16.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
            is com.guidetradeai.viewmodel.AgentUiState.Error -> {
                androidx.compose.material3.Text(
                    text = "Error: ${uiState.message}",
                    modifier = Modifier.padding(16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                )
            }
            else -> {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Ask the agent...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            androidx.compose.material3.Button(
                onClick = { if (query.isNotBlank()) viewModel.sendMessage(query) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Send")
            }
        }
    }
}
