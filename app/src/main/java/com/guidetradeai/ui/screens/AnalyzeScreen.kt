package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.EmptyState
import com.guidetradeai.ui.components.ErrorState
import com.guidetradeai.ui.components.LoadingState
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AnalyzeViewModel

@Composable
fun AnalyzeScreen(navController: NavHostController, viewModel: AnalyzeViewModel) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Analyze",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )

        when (uiState) {
            is com.guidetradeai.viewmodel.AnalyzeUiState.Loading -> {
                LoadingState(modifier = Modifier.fillMaxSize())
            }
            is com.guidetradeai.viewmodel.AnalyzeUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.reset() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.AnalyzeUiState.Success -> {
                if (uiState.result.signal != null) {
                    com.guidetradeai.ui.components.SignalCard(
                        signal = uiState.result.signal,
                        onClick = {
                            navController.navigate(NavRoutes.SignalDetails.route(uiState.result.signal!!.id))
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                uiState.result.content?.let { content ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = content,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                }
            }
            else -> {
                EmptyState(
                    title = "Select a market and asset to analyze",
                    description = "Use the options below to configure your analysis.",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Market", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        val markets = listOf("crypto", "forex", "stocks", "commodities")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            markets.forEach { market ->
                androidx.compose.material3.Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Text(
                        text = market.replaceFirstChar { it.uppercase() },
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Symbol", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        androidx.compose.material3.OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Search asset") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Timeframe", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        val timeframes = listOf("1m", "5m", "15m", "1h", "4h", "1D")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            timeframes.forEach { tf ->
                androidx.compose.material3.FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text(tf) },
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Analysis", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        val types = listOf("Signal", "Technical", "Market", "Risk", "Full Analysis")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            types.forEach { type ->
                androidx.compose.material3.Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Text(type, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text("Analyze", color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary)
        }
    }
}
