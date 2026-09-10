package com.guidetradeai.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

package com.guidetradeai.ui.components


@Composable
fun AssetRow(
    symbol: String,
    name: String,
    price: Double?,
    changePercent: Double?,
    onClick: (() -> Unit)? = null,
) {
    val changeColor = when {
        changePercent == null -> GuideTradeColors.TextSecondary
        changePercent >= 0 -> GuideTradeColors.Positive
        else -> GuideTradeColors.Negative
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
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
                Text(text = symbol, color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(text = name, color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            if (price != null) {
                Text(text = "$${"%.2f".format(price)}", color = GuideTradeColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            if (changePercent != null) {
                val sign = if (changePercent >= 0) "+" else ""
                Text(text = "$sign${"%.2f".format(changePercent)}%", color = changeColor, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AvatarInitials(
    name: String,
    size: Int = 40,
) {
    val initials = name.trim().split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(GuideTradeColors.PrimaryPurple, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = GuideTradeColors.White,
            fontSize = (size * 0.4f).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun MarketStatusIndicator(
    isOpen: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(if (isOpen) GuideTradeColors.Positive else GuideTradeColors.Negative, CircleShape),
        )
        Text(
            text = if (isOpen) "Open" else "Closed",
            color = if (isOpen) GuideTradeColors.Positive else GuideTradeColors.Negative,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}