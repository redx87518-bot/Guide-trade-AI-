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
import com.guidetradeai.domain.model.Signal
import com.guidetradeai.ui.components.SignalBadge

@Composable
fun SignalDetailsScreen(navController: NavHostController, signalId: String) {
    val signal = com.guidetradeai.domain.model.Signal()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = signal.symbol.ifBlank { "Signal" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "${signal.market.replaceFirstChar { it.uppercase() }} • ${signal.timeframe}",
                    fontSize = 13.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SignalBadge(direction = signal.direction)
        }

        if (signal.summary != null) {
            Section(title = "Summary", body = signal.summary)
        }
        if (signal.analysis != null) {
            Section(title = "Analysis", body = signal.analysis)
        }
        if (signal.risk != null) {
            Section(title = "Risk", body = signal.risk)
        }
        if (signal.marketContext != null) {
            Section(title = "Market Context", body = signal.marketContext)
        }
        if (signal.technicalInfo != null) {
            Section(title = "Technical Information", body = signal.technicalInfo)
        }
        if (signal.why != null) {
            Section(title = "Why This Signal", body = signal.why)
        }

        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun Section(title: String, body: String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = body,
            fontSize = 13.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
