package com.guidetradeai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.guidetradeai.ui.theme.GuideTradeColors
@Composable
fun ExpandableSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(200),
        label = "expand_rotation",
    )
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title.uppercase(),
                color = GuideTradeColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
            )
            Icon(
                imageVector = androidx.compose.material.icons.filled.ArrowForward,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = GuideTradeColors.MutedText,
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer(rotationZ = rotation),
        }
        if (isExpanded) {
            content()
    }
}
fun CopyableText(
    text: String,
    color: Color = GuideTradeColors.TextPrimary,
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        TextButton(
            onClick = {
                val clipboard = androidx.compose.ui.platform.LocalContext.current
                    .getService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Copy", text))
                android.widget.Toast.makeText(android.compose.ui.platform.LocalContext.current, "Copied", android.widget.Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.height(24.dp),
                imageVector = androidx.compose.material.icons.filled.ContentCopy,
                contentDescription = "Copy",
                modifier = Modifier.size(14.dp),
fun GradientCard(
    colors: List<Color> = listOf(
        GuideTradeColors.PrimaryPurple.copy(alpha = 0.15f),
        GuideTradeColors.PrimarySurface,
    ),
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(colors = colors),
                shape = RoundedCornerShape(16.dp),
            .border(1.dp, GuideTradeColors.SubtleBorder, RoundedCornerShape(16.dp)),
        content()
