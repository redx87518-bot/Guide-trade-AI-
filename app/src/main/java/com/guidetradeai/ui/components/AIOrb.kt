package com.guidetradeai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.guidetradeai.voice.VoiceState

@Composable
fun AIOrb(
    state: VoiceState,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    onClick: () -> Unit = {},
) {
    val transition = updateTransition(targetState = state, label = "orb_state_transition")

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

    val gradientShift by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        label = "orb_gradient",
    ) { currentState ->
        when (currentState) {
            VoiceState.IDLE -> 0f
            VoiceState.LISTENING -> 1f
            VoiceState.PROCESSING -> 2f
            VoiceState.SPEAKING -> 3f
            VoiceState.ERROR -> 0f
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "orb_idle_pulse")
    val idlePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "idle_pulse_scale",
    )

    val finalScale = if (state == VoiceState.IDLE) scale * idlePulse else scale

    val gradientColors = when (state) {
        VoiceState.IDLE -> listOf(
            Color(0xFF6366F1),
            Color(0xFF4345D8),
            Color(0xFF10B981),
        )
        VoiceState.LISTENING -> listOf(
            Color(0xFF38BDF8),
            Color(0xFF0EA5E9),
            Color(0xFF6366F1),
        )
        VoiceState.PROCESSING -> listOf(
            Color(0xFFF59E0B),
            Color(0xFFD97706),
            Color(0xFF6366F1),
        )
        VoiceState.SPEAKING -> listOf(
            Color(0xFF10B981),
            Color(0xFF059669),
            Color(0xFF6366F1),
        )
        VoiceState.ERROR -> listOf(
            Color(0xFFEF4444),
            Color(0xFFDC2626),
            Color(0xFF6366F1),
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer(
                scaleX = finalScale,
                scaleY = finalScale,
                alpha = pulseAlpha,
            )
            .clickable(onClick = onClick)
            .background(
                brush = Brush.radialGradient(
                    colors = gradientColors,
                    center = Offset(size.value / 2, size.value / 2),
                    radius = size.value / 2,
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.55f)
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
fun VoiceStateIndicator(state: VoiceState) {
    val dotCount = 3
    val transition = updateTransition(targetState = state, label = "voice_indicator")

    Box(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val alpha by transition.animateFloat(
            transitionSpec = { tween(300) },
            label = "indicator_alpha",
        ) { currentState ->
            when (currentState) {
                VoiceState.IDLE -> 0.4f
                VoiceState.LISTENING -> 1.0f
                VoiceState.PROCESSING -> 1.0f
                VoiceState.SPEAKING -> 1.0f
                VoiceState.ERROR -> 1.0f
            }
        }

        val color by transition.animateColor(
            transitionSpec = { tween(300) },
            label = "indicator_color",
        ) { currentState ->
            when (currentState) {
                VoiceState.IDLE -> Color(0xFF6366F1)
                VoiceState.LISTENING -> Color(0xFF38BDF8)
                VoiceState.PROCESSING -> Color(0xFFF59E0B)
                VoiceState.SPEAKING -> Color(0xFF10B981)
                VoiceState.ERROR -> Color(0xFFEF4444)
            }
        }
    }
}
