package com.guidetradeai.ui.components

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.voice.VoiceState
import com.guidetradeai.ui.theme.GuideTradeColors
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

@Composable
fun AgentOrb(
    state: VoiceState,
    modifier: Modifier = Modifier,
    sizeDp: Float = 180f,
    onClick: () -> Unit = {},
) {
    val transition = updateTransition(targetState = state, label = "orb_state")

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "orb_scale",
    ) { currentState ->
        when (currentState) {
            VoiceState.IDLE -> 1.0f
            VoiceState.LISTENING -> 1.12f
            VoiceState.PROCESSING -> 0.92f
            VoiceState.SPEAKING -> 1.05f
            VoiceState.ERROR -> 1.0f
        }
    }

    val pulseAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "orb_alpha",
    ) { currentState ->
        when (currentState) {
            VoiceState.IDLE -> 0.85f
            VoiceState.LISTENING -> 1.0f
            VoiceState.PROCESSING -> 0.65f
            VoiceState.SPEAKING -> 1.0f
            VoiceState.ERROR -> 0.4f
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "orb_idle_pulse")
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "idle_pulse_scale",
    )

    val finalScale = if (state == VoiceState.IDLE) scale * idlePulse else scale

    val gradientColors = when (state) {
        VoiceState.IDLE -> listOf(
            GuideTradeColors.PrimaryPurple,
            GuideTradeColors.DeepPurple,
            GuideTradeColors.SecondaryAccent,
        )
        VoiceState.LISTENING -> listOf(
            GuideTradeColors.Information,
            GuideTradeColors.PrimaryPurple,
            GuideTradeColors.BrightPurple,
        )
        VoiceState.PROCESSING -> listOf(
            GuideTradeColors.Warning,
            GuideTradeColors.PrimaryPurple,
            GuideTradeColors.BrightPurple,
        )
        VoiceState.SPEAKING -> listOf(
            GuideTradeColors.SecondaryAccent,
            GuideTradeColors.PrimaryPurple,
            GuideTradeColors.BrightPurple,
        )
        VoiceState.ERROR -> listOf(
            GuideTradeColors.Negative,
            GuideTradeColors.PrimaryPurple,
            GuideTradeColors.DeepPurple,
        )
    }

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .graphicsLayer(
                scaleX = finalScale,
                scaleY = finalScale,
                alpha = pulseAlpha,
            )
            .clickable(onClick = onClick)
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = Offset(sizeDp / 2, sizeDp / 2),
                    radius = sizeDp / 2,
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp.dp * 0.55f)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

@Composable
fun MiniAgentOrb(
    state: VoiceState,
    modifier: Modifier = Modifier,
    sizeDp: Float = 48f,
    onClick: () -> Unit = {},
) {
    AgentOrb(
        state = state,
        modifier = modifier,
        sizeDp = sizeDp,
        onClick = onClick,
    )
}