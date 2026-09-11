package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.guidetradeai.ui.theme.GuideTradeColors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

@Composable
fun AssetRow(
    symbol: String,
    name: String,
    price: Double?,
    changePercent: Double?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isPositive = (changePercent ?: 0.0) >= 0
    val changeColor = if (isPositive) GuideTradeColors.Positive else GuideTradeColors.Negative
    val sign = if (isPositive) "+" else ""

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(GuideTradeColors.PrimarySurface, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = symbol.take(2).uppercase(),
                    color = GuideTradeColors.BrightPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = symbol,
                    color = GuideTradeColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = name,
                    color = GuideTradeColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            if (price != null) {
                Text(
                    text = "$${"%.2f".format(price)}",
                    color = GuideTradeColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (changePercent != null) {
                Text(
                    text = "${sign}${"%.2f".format(changePercent)}%",
                    color = changeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
fun AvatarInitials(
    name: String?,
    modifier: Modifier = Modifier,
    size: Int = 56,
) {
    val initials = name?.filter { it.isLetter() }?.take(2)?.uppercase() ?: "U"
    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GuideTradeColors.BrightPurple,
                        GuideTradeColors.PrimaryPurple,
                    ),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = GuideTradeColors.White,
            fontSize = (size * 0.35).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun MarketStatusIndicator(
    status: String,
    modifier: Modifier = Modifier,
) {
    val (color, label) = when (status.lowercase()) {
        "active", "open", "trading" -> GuideTradeColors.Positive to "Active"
        "closed", "inactive" -> GuideTradeColors.MutedText to "Closed"
        "pre-market", "premarket" -> GuideTradeColors.Warning to "Pre-Market"
        "after-hours", "afterhours" -> GuideTradeColors.Information to "After Hours"
        else -> GuideTradeColors.Information to status
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}