package com.guidetradeai.ui.screens

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
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.height
import androidx.navigation.NavHostController

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GuideTradeColors.Background),
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
                        animation = tween(1800, easing = LinearOutSlowInEasing),
                    label = "orb_alpha",
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .shadow(
                            elevation = 40.dp,
                            shape = CircleShape,
                            spotColor = GuideTradeColors.PrimaryPurple.copy(alpha = 0.45f),
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GuideTradeColors.PrimaryPurple.copy(alpha = 0.25f),
                                    GuideTradeColors.DeepPurple.copy(alpha = 0.25f),
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
                            color = Color.White.copy(alpha = alpha * 0.12f),
                            radius = baseRadius,
                            brush = Brush.radialGradient(
                                    GuideTradeColors.BrightPurple.copy(alpha = 0.8f * alpha),
                                    GuideTradeColors.PrimaryPurple.copy(alpha = 0.6f * alpha),
                                    GuideTradeColors.Background,
                                center = Offset(center.x * 0.35f, center.y * 0.3f),
                            radius = baseRadius * 0.85f,
                            color = Color.White.copy(alpha = 0.18f),
                            radius = baseRadius * 0.16f,
                            center = Offset(center.x * 0.3f, center.y * 0.25f),
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "GUIDETRADE AI",
                    color = GuideTradeColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                Spacer(modifier = Modifier.height(12.dp))
                    text = tagline.take(typedText),
                    color = GuideTradeColors.BrightPurple,
                    fontWeight = FontWeight.W500,
                    fontSize = 12.sp,
                    letterSpacing = 0.12.sp,
            }
}