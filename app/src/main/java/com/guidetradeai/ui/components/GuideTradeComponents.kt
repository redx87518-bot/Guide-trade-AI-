package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
<<<<<<< ours
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
=======
>>>>>>> theirs
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors

@Composable
fun GuideTradeCard(
    modifier: Modifier = Modifier,
    containerColor: Color = GuideTradeColors.CardSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { onClick?.invoke() },
    ) {
        content()
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val bgColor = when (color) {
        GuideTradeColors.Positive -> GuideTradeColors.PositiveSoft
        GuideTradeColors.Negative -> GuideTradeColors.NegativeSoft
        GuideTradeColors.Warning -> GuideTradeColors.WarningSoft
        else -> GuideTradeColors.SecondarySurface
    }
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            color = GuideTradeColors.MutedText,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        )
        if (action != null) {
            action()
        }
    }
}

@Composable
fun PriceText(
    value: Double?,
    modifier: Modifier = Modifier,
    color: Color = GuideTradeColors.TextPrimary,
    fontSize: Float = 18f,
) {
    if (value == null) {
        Text(
            text = "—",
            color = GuideTradeColors.MutedText,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
        )
    } else {
        Text(
            text = "$${"%.2f".format(value)}",
            color = color,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
        )
    }
}
<<<<<<< ours
=======

@Composable
fun ChangeText(
    change: Double?,
    changePercent: Double?,
    modifier: Modifier = Modifier,
) {
    if (change == null && changePercent == null) return
    val isPositive = (change ?: 0.0) >= 0
    val color = if (isPositive) GuideTradeColors.Positive else GuideTradeColors.Negative
    val sign = if (isPositive) "+" else ""
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (changePercent != null) {
            Text(
                text = "${sign}${"%.2f".format(changePercent)}%",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        if (change != null && changePercent == null) {
            Text(
                text = "${sign}${"%.2f".format(change)}",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun TrendIndicator(
    trend: String,
    modifier: Modifier = Modifier,
) {
    val (color, icon) = when (trend.lowercase()) {
        "up", "bullish", "positive", "rise", "rising" -> GuideTradeColors.Positive to "▲"
        "down", "bearish", "negative", "fall", "falling" -> GuideTradeColors.Negative to "▼"
        else -> GuideTradeColors.MutedText to "•"
    }
    Text(
        text = icon,
        color = color,
        fontSize = 12.sp,
        modifier = modifier,
    )
}
>>>>>>> theirs
