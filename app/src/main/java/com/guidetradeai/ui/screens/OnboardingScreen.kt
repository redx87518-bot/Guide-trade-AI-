package com.guidetradeai.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val onboardingData = listOf(
        OnboardingPageData(
            title = "AI Trading Research",
            description = "Ask the AI to research markets and explain trading concepts.",
        ),
        OnboardingPageData(
            title = "Talk Naturally",
            description = "Use your voice to interact with the AI and get spoken responses.",
        ),
        OnboardingPageData(
            title = "Your Research, Organized",
            description = "Chat sessions, research history, and Telegram notifications keep everything in order.",
        ),
    )
    val pagerState = rememberPagerState { onboardingData.size }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val data = onboardingData[page]
            OnboardingPage(
                title = data.title,
                description = data.description,
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            TextButton(
                onClick = {
                    val app = context.applicationContext as com.guidetradeai.GuideTradeApp
                    app.getSharedPreferences().edit()
                        .putBoolean("onboarding_complete", true)
                        .apply()
                    navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.LOGIN) {
                        popUpTo(com.guidetradeai.ui.navigation.NavRoutes.ONBOARDING) { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "SKIP",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingData.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        val app = context.applicationContext as com.guidetradeai.GuideTradeApp
                        app.getSharedPreferences().edit()
                            .putBoolean("onboarding_complete", true)
                            .apply()
                        navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.LOGIN) {
                            popUpTo(com.guidetradeai.ui.navigation.NavRoutes.ONBOARDING) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = if (pagerState.currentPage < onboardingData.size - 1) "NEXT" else "GET STARTED",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF4345D8),
                            Color(0xFF10B981),
                        ),
                        center = Offset(60f, 60f),
                        radius = 60f,
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {}

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W600,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
)
