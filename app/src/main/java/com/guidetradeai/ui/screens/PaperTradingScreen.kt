package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
fun PaperTradingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(
            text = "PAPER TRADING",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "SIMULATED FUNDS ONLY",
            fontSize = 11.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Balance", fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$100,000.00", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Cash", fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$100,000.00", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Open Positions", fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                Text("0", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Today's P&L", fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$0.00", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Tabs", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Overview", "Positions", "Orders", "History").forEach { tab ->
                androidx.compose.material3.FilterChip(
                    selected = tab == "Overview",
                    onClick = {},
                    label = { Text(tab, fontSize = 12.sp) },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No open positions",
            fontSize = 13.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
