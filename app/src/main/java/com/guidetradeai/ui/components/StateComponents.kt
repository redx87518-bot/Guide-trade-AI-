package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors
import androidx.compose.material3.Button

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = GuideTradeColors.BrightPurple, strokeWidth = 3.dp)
            Text(text = message, color = GuideTradeColors.TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(48.dp))
            }
            Text(text = title, color = GuideTradeColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            if (description != null) {
                Text(text = description, color = GuideTradeColors.TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
            if (action != null) {
                action()
            }
        }
    }
}

@Composable
fun ErrorState(
    title: String,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = title, color = GuideTradeColors.Negative, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            if (message != null) {
                Text(text = message, color = GuideTradeColors.TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
            if (onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text("Retry", color = GuideTradeColors.BrightPurple)
                }
            }
        }
    }
}
