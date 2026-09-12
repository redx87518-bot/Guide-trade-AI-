package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.guidetradeai.viewmodel.McpViewModel

@Composable
fun McpConnectionsScreen(navController: NavHostController) {
    val viewModel: McpViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val uiState = viewModel.uiState.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "MCP Connectors",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "Connect market, wallet and account data to GuideTrade.",
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )

        when (uiState) {
            is com.guidetradeai.viewmodel.McpUiState.Loading -> {
                LoadingState(modifier = Modifier.fillMaxSize())
            }
            is com.guidetradeai.viewmodel.McpUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { viewModel.loadData() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is com.guidetradeai.viewmodel.McpUiState.Success -> {
                val catalog = uiState.catalog
                val connections = uiState.connections
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    if (catalog.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "AVAILABLE CONNECTORS",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        items(catalog) { item ->
                            McpConnectorCard(item = item, isConnected = false, onClick = {})
                        }
                    }
                    if (connections.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "CONNECTED",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        items(connections) { item ->
                            McpConnectorCard(item = item, isConnected = true, onClick = {})
                        }
                    }
                    if (catalog.isEmpty() && connections.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No connectors available",
                                description = "Check back later for available integrations.",
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
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

@Composable
private fun McpConnectorCard(item: com.guidetradeai.data.repository.McpCatalogItem, isConnected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Surface(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 12.dp),
                ) {
                    Text(
                        text = item.name.firstOrNull()?.toString() ?: "?",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    )
                }
                Column {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.permission,
                        fontSize = 11.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    )
                }
            }
            androidx.compose.material3.TextButton(onClick = onClick) {
                Text(if (isConnected) "Open" else "Add")
            }
        }
    }
}
