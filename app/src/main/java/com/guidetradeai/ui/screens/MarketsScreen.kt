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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeTextField
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.viewmodel.MarketsViewModel
import com.guidetradeai.viewmodel.MarketsUiState
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MarketsScreen(
    navController: NavHostController,
    viewModel: MarketsViewModel = MarketsViewModel(),
) {
    var symbol by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("BTC") }
    var market by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("crypto") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Markets", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Lookup symbols and market context", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(20.dp))
        GuideTradeTextField(
            value = symbol,
            onValueChange = { symbol = it },
            label = "Symbol",
        )
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeTextField(
            value = market,
            onValueChange = { market = it },
            label = "Market",
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "Load Market Data",
            onClick = { viewModel.loadMarketData(market, symbol) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is MarketsUiState.Loading -> Text("Loading...")
            is MarketsUiState.Success -> Text("Result: ${(uiState as MarketsUiState.Success).data}")
            is MarketsUiState.Error -> Text("Error: ${(uiState as MarketsUiState.Error).message}")
        }
    }
}
