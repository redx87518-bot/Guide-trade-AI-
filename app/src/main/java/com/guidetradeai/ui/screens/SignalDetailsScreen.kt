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
import com.guidetradeai.domain.model.Signal
import com.guidetradeai.ui.components.SignalBadge

@Composable
fun SignalDetailsScreen(navController: NavHostController, signalId: String) {
    val signal = Signal()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = signal.symbol.ifBlank { "Signal" },
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = "${signal.market.replaceFirstChar { it.uppercase() }} • ${signal.timeframe}",
            fontSize = 14.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SignalBadge(direction = signal.direction)

        Spacer(modifier = Modifier.height(8.dp))

        if (signal.summary != null) {
            Text(
                text = "Summary",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = signal.summary,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (signal.analysis != null) {
            Text(
                text = "Analysis",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = signal.analysis,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (signal.risk != null) {
            Text(
                text = "Risk",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = signal.risk,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (signal.marketContext != null) {
            Text(
                text = "Market Context",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = signal.marketContext,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (signal.technicalInfo != null) {
            Text(
                text = "Technical Information",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = signal.technicalInfo,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (signal.why != null) {
            Text(
                text = "Why This Signal",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = signal.why,
                fontSize = 14.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Back")
        }
    }
}
