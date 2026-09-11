package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.domain.model.Signal

@Composable
fun SignalCard(
    signal: Signal,
    onClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = Modifier,
) {
    val directionColor = when (signal.direction.lowercase()) {
        "bullish" -> Color(0xFF22C55E)
        "bearish" -> Color(0xFFEF4444)
        else -> Color(0xFFF59E0B)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1525)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = signal.symbol.ifBlank { signal.name.ifBlank { "Unknown" } },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFE8F4FF),
                    )
                    Text(
                        text = "${signal.market.replaceFirstChar { it.uppercase() }} • ${signal.timeframe}",
                        fontSize = 12.sp,
                        color = Color(0xFF7A9CC0),
                    )
                }
                SignalBadge(direction = signal.direction)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Strength",
                    fontSize = 12.sp,
                    color = Color(0xFF7A9CC0),
                )
                Text(
                    text = "${(signal.strength * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = directionColor,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF111E33)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(signal.strength.coerceIn(0.0, 1.0).toFloat())
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(directionColor),
                )
            }

            if (signal.entry != null || signal.currentPrice != null) {
                Text(
                    text = buildString {
                        append("Entry: ")
                        append(signal.entry?.toString() ?: "Not available")
                        if (signal.currentPrice != null) {
                            append("  |  Current: ")
                            append(signal.currentPrice.toString())
                        }
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF7A9CC0),
                )
            }

            if (signal.invalidation != null || signal.target1 != null) {
                Text(
                    text = buildString {
                        append("Invalidation: ")
                        append(signal.invalidation?.toString() ?: "Not available")
                        if (signal.target1 != null) {
                            append("  |  Target: ")
                            append(signal.target1.toString())
                        }
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF7A9CC0),
                )
            }

            Text(
                text = "Updated ${if (signal.updatedAt.isNotBlank()) signal.updatedAt else "recently"}",
                fontSize = 11.sp,
                color = Color(0xFF7A9CC0),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SignalBadge(direction: String) {
    val color = when (direction.lowercase()) {
        "bullish" -> Color(0xFF22C55E)
        "bearish" -> Color(0xFFEF4444)
        "neutral" -> Color(0xFFF59E0B)
        else -> Color(0xFF7A9CC0)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = direction.ifBlank { "NEUTRAL" }.uppercase(),
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
