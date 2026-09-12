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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun McpConnectionsScreen(navController: NavHostController) {
    val categories = listOf(
        McpCategory("MARKET INTELLIGENCE", listOf(
            McpItem("NORTH7", "Market intelligence & signals", "Read Only", Icons.Default.ShowChart),
        )),
        McpCategory("WALLETS", listOf(
            McpItem("Crypto Wallet", "Read wallet balances and address data", "Read Only", Icons.Default.AccountBalanceWallet),
        )),
        McpCategory("EXCHANGES", listOf(
            McpItem("Exchange Account", "Read-only account data, demo/paper support", "Read Only", Icons.Default.TrendingUp),
        )),
        McpCategory("BROKERS", listOf(
            McpItem("Broker Account", "Read-only account information", "Read Only", Icons.Default.Business),
        )),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "MCP Connectors",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Connect market, wallet and account data to GuideTrade.",
                    fontSize = 13.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        categories.forEach { category ->
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = category.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            items(category.items) { item ->
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(end = 12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
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
                        androidx.compose.material3.TextButton(onClick = {}) {
                            Text("Add")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

data class McpCategory(val title: String, val items: List<McpItem>)
data class McpItem(
    val name: String,
    val description: String,
    val permission: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)
