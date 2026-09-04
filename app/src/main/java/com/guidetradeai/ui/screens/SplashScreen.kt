package com.guidetradeai.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.viewmodel.AuthViewModel
import androidx.compose.runtime.setValue

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val app = context.applicationContext as com.guidetradeai.GuideTradeApp

    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500)

        val isAuthenticated = app.isUserAuthenticated
        if (isAuthenticated) {
            navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.HOME) {
                popUpTo(com.guidetradeai.ui.navigation.NavRoutes.SPLASH) { inclusive = true }
            }
        } else {
            val onboardingComplete = app.getSharedPreferences().getBoolean("onboarding_complete", false)
            if (onboardingComplete) {
                navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.LOGIN) {
                    popUpTo(com.guidetradeai.ui.navigation.NavRoutes.SPLASH) { inclusive = true }
                }
            } else {
                navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.ONBOARDING) {
                    popUpTo(com.guidetradeai.ui.navigation.NavRoutes.SPLASH) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1117),
                        Color(0xFF1A1D2E),
                        Color(0xFF0F1117),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "splash_orb")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.85f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2200, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "orb_scale",
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.75f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "orb_alpha",
            )

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .shadow(32.dp, CircleShape, spotColor = Color(0xFF6366F1).copy(alpha = 0.5f))
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6),
                                Color(0xFF10B981),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = alpha * 0.15f),
                        radius = size.minDimension / 2f,
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "GUIDE TRADE",
                color = Color.White,
                fontWeight = FontWeight.W700,
                fontSize = 38.sp,
                letterSpacing = 2.sp,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "AI RESEARCH ASSISTANT",
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.W400,
                fontSize = 13.sp,
                letterSpacing = 6.sp,
            )
        }
    }
}
