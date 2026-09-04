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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.icons.filled.MoreVert
import androidx.compose.material3.icons.filled.PlayCircle
import androidx.compose.material3.icons.filled.Save
import androidx.compose.material3.icons.filled.Share
import androidx.compose.material3.icons.filled.Speaker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guidetradeai.utils.Constants

@Composable
fun AIOrb(
    state: com.guidetradeai.voice.VoiceState,
    modifier: Modifier = Modifier,
    sizeDp: Float = 180f,
    onClick: () -> Unit = {},
) {
    val transition = updateTransition(targetState = state, label = "orb_state_transition")

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "orb_scale",
    ) { currentState ->
        when (currentState) {
            com.guidetradeai.voice.VoiceState.IDLE -> 1.0f
            com.guidetradeai.voice.VoiceState.LISTENING -> 1.12f
            com.guidetradeai.voice.VoiceState.PROCESSING -> 0.92f
            com.guidetradeai.voice.VoiceState.SPEAKING -> 1.05f
            com.guidetradeai.voice.VoiceState.ERROR -> 1.0f
        }
    }

    val pulseAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "orb_alpha",
    ) { currentState ->
        when (currentState) {
            com.guidetradeai.voice.VoiceState.IDLE -> 0.85f
            com.guidetradeai.voice.VoiceState.LISTENING -> 1.0f
            com.guidetradeai.voice.VoiceState.PROCESSING -> 0.65f
            com.guidetradeai.voice.VoiceState.SPEAKING -> 1.0f
            com.guidetradeai.voice.VoiceState.ERROR -> 0.4f
        }
    }

    val gradientShift by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500) },
        label = "orb_gradient",
    ) { currentState ->
        when (currentState) {
            com.guidetradeai.voice.VoiceState.IDLE -> 0f
            com.guidetradeai.voice.VoiceState.LISTENING -> 1f
            com.guidetradeai.voice.VoiceState.PROCESSING -> 2f
            com.guidetradeai.voice.VoiceState.SPEAKING -> 3f
            com.guidetradeai.voice.VoiceState.ERROR -> 0f
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

    val finalScale = if (state == com.guidetradeai.voice.VoiceState.IDLE) scale * idlePulse else scale

    val gradientColors = when (state) {
        com.guidetradeai.voice.VoiceState.IDLE -> listOf(
            Color(0xFF6366F1),
            Color(0xFF4345D8),
            Color(0xFF10B981),
        )
        com.guidetradeai.voice.VoiceState.LISTENING -> listOf(
            Color(0xFF38BDF8),
            Color(0xFF0EA5E9),
            Color(0xFF6366F1),
        )
        com.guidetradeai.voice.VoiceState.PROCESSING -> listOf(
            Color(0xFFF59E0B),
            Color(0xFFD97706),
            Color(0xFF6366F1),
        )
        com.guidetradeai.voice.VoiceState.SPEAKING -> listOf(
            Color(0xFF10B981),
            Color(0xFF059669),
            Color(0xFF6366F1),
        )
        com.guidetradeai.voice.VoiceState.ERROR -> listOf(
            Color(0xFFEF4444),
            Color(0xFFDC2626),
            Color(0xFF6366F1),
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
                shape = RoundedCornerShape(sizeDp / 2),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp.dp * 0.55f)
                .clip(RoundedCornerShape(sizeDp / 2))
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
