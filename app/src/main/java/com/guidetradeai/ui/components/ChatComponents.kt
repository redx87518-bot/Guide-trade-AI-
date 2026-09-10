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
fun MessageCard(
    role: String,
    content: String,
    timestamp: String? = null,
    onSpeakerClick: (() -> Unit)? = null,
    onSaveClick: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val isUser = role == "user"
    val backgroundColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = if (isUser) 16.dp else 4.dp,
            topEnd = if (isUser) 4.dp else 16.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = content,
                color = contentColor,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
            if (timestamp != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timestamp,
                    color = if (isUser) GuideTradeColors.TextPrimary else GuideTradeColors.MutedText,
                    fontSize = 10.sp,
                )
            }
        }
    }
}