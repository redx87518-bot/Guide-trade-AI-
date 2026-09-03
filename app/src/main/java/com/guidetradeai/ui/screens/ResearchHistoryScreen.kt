package com.guidetradeai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.icons.filled.Delete
import androidx.compose.material3.icons.filled.Share
import androidx.compose.material3.icons.filled.Speaker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.domain.model.ResearchResult
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.utils.formatDate
import com.guidetradeai.utils.truncate
import com.guidetradeai.viewmodel.ResearchDetailUiState
import com.guidetradeai.viewmodel.ResearchDetailViewModel
import com.guidetradeai.viewmodel.ResearchHistoryUiState
import com.guidetradeai.viewmodel.ResearchViewModel

@Composable
fun ResearchHistoryScreen(
    navController: NavHostController,
    researchViewModel: ResearchViewModel = viewModel(),
) {
    val uiState by researchViewModel.uiState.collectAsState()
    val results = (uiState as? ResearchHistoryUiState.Success)?.results ?: emptyList()

    LaunchedEffect(Unit) {
        researchViewModel.loadHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp, 24.dp, 24.dp, 80.dp),
    ) {
        Text(
            text = "Research History",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.W600,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (results.isEmpty()) {
            if (uiState is ResearchHistoryUiState.Loading) {
                Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text(
                    text = "No research results yet. Ask the AI a question in chat.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(results, key = { it.id }) { result ->
                    com.guidetradeai.ui.screens.ResearchCard(
                        result = result,
                        onOpen = {
                            navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.researchDetailRoute(result.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun ResearchCard(
    result: ResearchResult,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )
            result.asset?.let { asset ->
                Text(
                    text = asset,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.response.truncate(120),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = result.createdAt.formatDate("MMM dd, yyyy"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onOpen) {
                    Text("Open", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
            }
        }
    }
}
