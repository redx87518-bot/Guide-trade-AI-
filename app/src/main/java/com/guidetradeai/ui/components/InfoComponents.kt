package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors
@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tint: Color = GuideTradeColors.TextSecondary,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = GuideTradeColors.TextSecondary,
                fontSize = 13.sp,
        }
        Text(
            text = value,
            color = GuideTradeColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
fun InfoCard(
    title: String,
    subtitle: String? = null,
    tint: Color = GuideTradeColors.BrightPurple,
    Column(
        modifier = modifier
            .background(GuideTradeColors.SecondarySurface, RoundedCornerShape(12.dp))
            .padding(12.dp),
            text = title.uppercase(),
            color = GuideTradeColors.MutedText,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        Spacer(modifier = Modifier.height(4.dp))
            color = tint,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        if (subtitle != null) {
                text = subtitle,
                fontSize = 11.sp,
fun RiskGauge(
    riskLevel: String,
    val (color, _) = when (riskLevel.lowercase()) {
        "low" -> GuideTradeColors.Positive to 0.25f
        "moderate" -> GuideTradeColors.Warning to 0.5f
        "high" -> GuideTradeColors.Negative to 0.75f
        "extreme" -> GuideTradeColors.Negative to 1f
        else -> GuideTradeColors.Information to 0.5f
    Column(modifier = modifier) {
            text = "RISK LEVEL",
        Spacer(modifier = Modifier.height(6.dp))
        StatusBadge(text = riskLevel.uppercase(), color = color)
