package com.guidetradeai.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.ui.theme.AccentCyan
import com.guidetradeai.ui.theme.AccentPurple
import com.guidetradeai.ui.theme.AccentGlow
import com.guidetradeai.ui.theme.Background
import com.guidetradeai.ui.theme.TextPrimary
import com.guidetradeai.ui.theme.TextSecondary
import com.guidetradeai.viewmodel.ChatViewModel
import kotlinx.coroutines.delay

@Composable
fun OrbScreen(
    navController: NavHostController,
    chatViewModel: ChatViewModel = viewModel(),
) {
    val isListening by chatViewModel.isListening.collectAsState()
    val isSpeaking by chatViewModel.isSpeaking.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    val orbState = when {
        isLoading -> OrbState.PROCESSING
        isListening -> OrbState.LISTENING
        isSpeaking -> OrbState.SPEAKING
        else -> OrbState.IDLE
    }

    var transcript by remember { mutableStateOf("") }
    var aiPreview by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        val lastAi = messages.lastOrNull { it.role == "assistant" }
        if (lastAi != null) {
            aiPreview = lastAi.content.take(120)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentGlow, Color.Transparent),
                        radius = size.minDimension / 2f,
                    ),
                    radius = size.minDimension / 2f,
                    center = Offset(size.width / 2f, size.height / 3f),
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center,
            ) {
                OrbCanvas(state = orbState)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = orbState.label,
                color = when (orbState) {
                    OrbState.LISTENING, OrbState.IDLE -> AccentCyan
                    OrbState.SPEAKING -> AccentPurple
                    OrbState.PROCESSING -> TextSecondary
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.08f,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (transcript.isNotBlank()) {
                Text(
                    text = transcript,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (aiPreview.isNotBlank()) {
                Text(
                    text = aiPreview,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
            }
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextSecondary,
            )
        }
    }
}

enum class OrbState(val label: String) {
    IDLE("TAP TO SPEAK"),
    LISTENING("LISTENING..."),
    PROCESSING("PROCESSING..."),
    SPEAKING("SPEAKING"),
}

@Composable
fun OrbCanvas(state: OrbState) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )

    val speed = when (state) {
        OrbState.LISTENING -> 1.5f
        OrbState.SPEAKING -> 1.2f
        else -> 1f
    }

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .rotate(rotation),
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val baseRadius = size.minDimension / 2f

        drawCircle(
            color = AccentCyan.copy(alpha = 0.12f * glowAlpha),
            radius = baseRadius * pulseScale * 1.25f,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentCyan.copy(alpha = 0.35f * glowAlpha), Color.Transparent),
                radius = baseRadius * pulseScale,
            ),
            radius = baseRadius * pulseScale,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentCyan.copy(alpha = 0.9f),
                    AccentPurple.copy(alpha = 0.7f),
                    Background,
                ),
                center = Offset(center.x * 0.35f, center.y * 0.3f),
            ),
            radius = baseRadius * 0.95f,
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            radius = baseRadius * 0.18f,
            center = Offset(center.x * 0.3f, center.y * 0.25f),
        )

        if (state == OrbState.LISTENING) {
            drawCircle(
                color = AccentCyan.copy(alpha = (1f - glowAlpha) * 0.4f),
                radius = baseRadius * (1.15f + (1f - glowAlpha) * 0.3f),
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        if (state == OrbState.SPEAKING) {
            for (i in 0..2) {
                val angle = (i * 120 + rotation * speed) % 360
                val rad = Math.toRadians(angle.toDouble())
                val x = center.x + (baseRadius * 1.15f * Math.cos(rad)).toFloat()
                val y = center.y + (baseRadius * 1.15f * Math.sin(rad)).toFloat()
                drawCircle(
                    color = AccentPurple.copy(alpha = 0.6f * glowAlpha),
                    radius = 3.dp.toPx(),
                    center = Offset(x, y),
                )
            }
        }
    }
}
