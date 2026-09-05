package com.guidetradeai.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.theme.AccentCyan
import com.guidetradeai.ui.theme.AccentPurple
import com.guidetradeai.ui.theme.Background
import com.guidetradeai.ui.theme.TextPrimary
import com.guidetradeai.viewmodel.AuthUiState
import com.guidetradeai.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val authUiState by authViewModel.uiState.collectAsState()
    var showContent by remember { mutableStateOf(false) }
    var exit by remember { mutableStateOf(false) }
    var typedText by remember { mutableIntStateOf(0) }
    val tagline = "INTELLIGENCE. PRECISION. EDGE."

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Loading) return@LaunchedEffect

        delay(100)
        showContent = true
        while (typedText < tagline.length) {
            delay(60)
            typedText++
        }
        delay(900)
        exit = true
        delay(600)
        val destination = if (authUiState is AuthUiState.Authenticated) "home" else "login"
        navController.navigate(destination) {
            popUpTo(0) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = showContent && !exit,
            enter = fadeIn(tween(700)),
            exit = fadeOut(tween(500)),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "splash_orb")
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

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .shadow(
                            elevation = 40.dp,
                            shape = CircleShape,
                            spotColor = AccentCyan.copy(alpha = 0.45f),
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AccentCyan.copy(alpha = 0.25f),
                                    AccentPurple.copy(alpha = 0.25f),
                                    Color.Transparent,
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val baseRadius = size.minDimension / 2f

                        drawCircle(
                            color = Color.White.copy(alpha = alpha * 0.12.sp),
                            radius = baseRadius,
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AccentCyan.copy(alpha = 0.8f * alpha),
                                    AccentPurple.copy(alpha = 0.6f * alpha),
                                    Background,
                                ),
                                center = Offset(center.x * 0.35f, center.y * 0.3f),
                            ),
                            radius = baseRadius * 0.85f,
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.18f),
                            radius = baseRadius * 0.16f,
                            center = Offset(center.x * 0.3f, center.y * 0.25f),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "QUAN",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = tagline.take(typedText),
                    color = AccentCyan,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    letterSpacing = 0.12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
