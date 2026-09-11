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
import com.guidetradeai.viewmodel.MarketsViewModel
import com.guidetradeai.viewmodel.MarketsUiState

@Composable
fun MarketsScreen(
    navController: NavHostController,
    viewModel: MarketsViewModel = MarketsViewModel(),
) {
    var symbol by remember { mutableStateOf("BTC") }
    var market by remember { mutableStateOf("crypto") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Markets", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = symbol,
            onValueChange = { symbol = it },
            label = { Text("Symbol") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = market,
            onValueChange = { market = it },
            label = { Text("Market") },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.loadMarketData(market, symbol) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Load Market Data")
        }

        when (uiState) {
            is MarketsUiState.Loading -> Text("Loading...")
            is MarketsUiState.Success -> {
                val data = (uiState as MarketsUiState.Success).data
                Text("Market Data: $data")
            }
            is MarketsUiState.Error -> {
                Text("Error: ${(uiState as MarketsUiState.Error).message}")
            }
        }
    }
}