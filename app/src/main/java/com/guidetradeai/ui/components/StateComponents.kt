package com.guidetradeai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading market intelligence...",
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(GuideTradeColors.PrimarySurface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            CircurProgressIndicator(
                color = GuideTradeColors.BrightPurple,
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = GuideTradeColors.TextSecondary,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String? = null,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(GuideTradeColors.PrimarySurface, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GuideTradeColors.PrimaryPurple.copy(alpha = 0.3f),
                                Color.Transparent,
                            ),
                        ),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(GuideTradeColors.PrimaryPurple, CircleShape),
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            color = GuideTradeColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        if (description != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = GuideTradeColors.TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(20.dp))
            action()
        }
    }
}

@Composable
fun ErrorState(
    title: String = "Something went wrong",
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(GuideTradeColors.NegativeSoft, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "!",
                color = GuideTradeColors.Negative,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = title,
            color = GuideTradeColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = GuideTradeColors.TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
        }
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(20.dp))
            TextButton(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Retry",
                    color = GuideTradeColors.BrightPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}