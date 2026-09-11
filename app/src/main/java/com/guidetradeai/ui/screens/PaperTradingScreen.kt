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
import com.guidetradeai.ui.components.PrimaryButton
import com.guidetradeai.viewmodel.PaperTradingUiState
import com.guidetradeai.viewmodel.PaperTradingViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun PaperTradingScreen(navController: NavHostController) {
    val viewModel: PaperTradingViewModel = PaperTradingViewModel()
    val state = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Paper Trading", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = "Load Dashboard",
            onClick = { viewModel.loadDashboard() },
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = "Back",
            onClick = { navController.popBackStack() },
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (state) {
            is PaperTradingUiState.Loading -> Text("Loading...")
            is PaperTradingUiState.Error -> Text("Error: ${state.message}")
            else -> Text("Dashboard ready")
        }
    }
}
