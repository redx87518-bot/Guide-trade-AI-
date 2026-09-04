package com.guidetradeai.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val onboardingData = listOf(
        OnboardingPageData(
            title = "AI Trading Research",
            description = "Ask the AI to research markets and explain trading concepts.",
            gradientColors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
        ),
        OnboardingPageData(
            title = "Talk Naturally",
            description = "Use your voice to interact with the AI and get spoken responses.",
            gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
        ),
        OnboardingPageData(
            title = "Your Research, Organized",
            description = "Chat sessions, research history, and Telegram notifications keep everything in order.",
            gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
        ),
    )
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
                gradientColors = data.gradientColors,
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(3) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (selected) 24.dp else 10.dp)
                            .shadow(if (selected) 8.dp else 0.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            ),
                    )
                }
            }
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
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
fun OnboardingPage(title: String, description: String, gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "orb_scale",
        )

        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .shadow(24.dp, CircleShape, spotColor = gradientColors.first().copy(alpha = 0.4f))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = gradientColors,
                    ),
                ),
            contentAlignment = Alignment.Center,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.W700,
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
    val gradientColors: List<Color> = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
)
