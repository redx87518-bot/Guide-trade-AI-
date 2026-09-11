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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.EmptyState
import com.guidetradeai.ui.components.ErrorState
import com.guidetradeai.ui.components.LoadingState
import com.guidetradeai.ui.components.SignalCard
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.SignalsViewModel

@Composable
fun SignalsScreen(navController: NavHostController, viewModel: SignalsViewModel) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Signals",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )

        when (uiState) {
            is com.guidetradeai.viewmodel.SignalsUiState.Loading -> {
                LoadingState(modifier = Modifier.fillMaxSize())
            }
            is com.guidetradeai.viewmodel.SignalsUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.SignalsUiState.Empty -> {
                EmptyState(
                    title = "No signals available",
                    description = uiState.message,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.SignalsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(uiState.signals) { signal ->
                        SignalCard(
                            signal = signal,
                            onClick = { navController.navigate(NavRoutes.SignalDetails.route(signal.id)) },
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            else -> {}
        }
    }
}
