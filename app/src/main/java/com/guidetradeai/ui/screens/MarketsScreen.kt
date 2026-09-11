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
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeTextField
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.viewmodel.MarketsUiState
import com.guidetradeai.viewmodel.MarketsViewModel

@Composable
fun MarketsScreen(
    navController: NavHostController,
    viewModel: MarketsViewModel = MarketsViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val symbolState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("BTC") }
    val marketState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("crypto") }

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
            value = symbolState.value,
            onValueChange = { symbolState.value = it },
            label = "Symbol",
        )
        Spacer(modifier = Modifier.height(8.dp))
        GuideTradeTextField(
            value = marketState.value,
            onValueChange = { marketState.value = it },
            label = "Market",
        )
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "Load Market Data",
            onClick = { viewModel.loadMarketData(marketState.value, symbolState.value) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is MarketsUiState.Loading -> Text("Loading...")
            is MarketsUiState.Success -> Text("Result: ${uiState.data}")
            is MarketsUiState.Error -> Text("Error: ${uiState.message}")
        }
    }
}
