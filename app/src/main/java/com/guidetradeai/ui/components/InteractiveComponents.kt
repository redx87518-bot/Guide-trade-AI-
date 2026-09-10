package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors
import androidx.compose.material3.Button

@Composable
fun ExpandableSection(
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = GuideTradeColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ArrowForward else Icons.Default.ArrowForward,
                contentDescription = null,
                tint = GuideTradeColors.MutedText,
                modifier = Modifier.size(18.dp),
            )
        }
        if (expanded) {
            Box(modifier = Modifier.padding(top = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun CopyableText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 3,
) {
    var copied by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GuideTradeColors.CardSurface, RoundedCornerShape(12.dp))
            .border(1.dp, GuideTradeColors.Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = GuideTradeColors.TextPrimary,
            fontSize = 14.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { /* TODO copy to clipboard */ }) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = GuideTradeColors.MutedText,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GuideTradeColors.PrimarySurface,
                        GuideTradeColors.ElevatedSurface,
                    ),
                ),
                shape = RoundedCornerShape(16.dp),
            )
            .border(1.dp, GuideTradeColors.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        content()
    }
}
