package com.guidetradeai.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.utils.toGreeting
import com.guidetradeai.viewmodel.AuthUiState
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.HomeUiState
import com.guidetradeai.viewmodel.HomeViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val context = LocalContext.current
    val app = context.applicationContext as com.guidetradeai.GuideTradeApp

    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)

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
            .background(Color(0xFF0F1117)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            OrbAnimation(modifier = Modifier.size(100.dp))

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "GUIDE TRADE",
                color = Color.White,
                fontWeight = FontWeight.W700,
                fontSize = 36.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI RESEARCH ASSISTANT",
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                letterSpacing = 4.sp,
            )
        }
    }
}

@Composable
fun OrbAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "splash_orb")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(2000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "orb_pulse",
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(1500),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "orb_alpha",
    )

    Box(
        modifier = modifier
            .size(100.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF6366F1),
            )
            drawCircle(
                color = Color(0xFF10B981).copy(alpha = 0.7f),
                radius = 30f,
            )
        }
    }
}

private fun getTimeBasedGreeting(): String {
    val hour = LocalDateTime.now().hour
    return when {
        hour in 5..11 -> "Good morning"
        hour in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}
