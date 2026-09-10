package com.guidetradeai.ui.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.ui.theme.GuideTradeColors
import com.guidetradeai.voice.VoiceState

@Composable
fun AgentOrb(
    state: VoiceState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb_scale",
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb_alpha",
    )

    val baseColor = when (state) {
        VoiceState.IDLE -> GuideTradeColors.PrimaryPurple
        VoiceState.LISTENING -> GuideTradeColors.BrightPurple
        VoiceState.PROCESSING -> GuideTradeColors.SecondaryAccent
        VoiceState.SPEAKING -> GuideTradeColors.Positive
        VoiceState.ERROR -> GuideTradeColors.Negative
    }

    Box(
        modifier = modifier
            .size(120.dp)
            .scale(scale)
            .shadow(
                elevation = 40.dp,
                shape = CircleShape,
                spotColor = baseColor.copy(alpha = 0.45f),
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.25f),
                        Color.Transparent,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f

            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.12f),
                radius = baseRadius,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        GuideTradeColors.BrightPurple.copy(alpha = 0.8f * alpha),
                        GuideTradeColors.PrimaryPurple.copy(alpha = 0.6f * alpha),
                        Color.Transparent,
                    ),
                    center = center,
                ),
                radius = baseRadius * 0.85f,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.18f),
                radius = baseRadius * 0.16f,
                center = center,
            )
        }
    }
}
