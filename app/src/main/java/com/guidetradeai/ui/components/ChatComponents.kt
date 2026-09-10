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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors

@Composable
fun ChatBubble(
    text: String,
    isUser: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isUser) GuideTradeColors.PrimaryPurple else GuideTradeColors.CardSurface
    val textColor = if (isUser) GuideTradeColors.White else GuideTradeColors.TextPrimary
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment,
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(16.dp))
                .border(1.dp, GuideTradeColors.Border, RoundedCornerShape(16.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp),
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Type a message...",
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GuideTradeColors.CardSurface, RoundedCornerShape(24.dp))
            .border(1.dp, GuideTradeColors.Border, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(placeholder, color = GuideTradeColors.MutedText) },
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            maxLines = 4,
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onSend, enabled = value.isNotBlank()) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Send",
                tint = if (value.isNotBlank()) GuideTradeColors.BrightPurple else GuideTradeColors.MutedText,
            )
        }
    }
}
