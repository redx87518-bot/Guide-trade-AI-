package com.guidetradeai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.domain.model.ResearchResult
import com.guidetradeai.ui.components.AIOrb
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.utils.formatDate
import com.guidetradeai.utils.toGreeting
import com.guidetradeai.viewmodel.AuthUiState
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.HomeUiState
import com.guidetradeai.viewmodel.HomeViewModel
import com.guidetradeai.voice.VoiceState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val homeViewModel: HomeViewModel = viewModel()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    androidx.compose.runtime.LaunchedEffect(authState) {
        if (authState is AuthUiState.Authenticated) {
            homeViewModel.loadHome()
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
        ) {
            // Top: Greeting
            val greeting = when (authState) {
                is AuthUiState.Authenticated -> {
                    val name = (authState as? AuthUiState.Authenticated)?.user?.fullName
                        ?.takeIf { !it.isNullOrEmpty() } ?: ""
                    val greetingWord = getTimeBasedGreeting()
                    "$greetingWord, ${name.toGreeting()}"
                }
                else -> "Welcome"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.W600,
                )
                val avatarUrl = (authState as? AuthUiState.Authenticated)?.user?.avatarUrl
                if (!avatarUrl.isNullOrEmpty()) {
                    coil.compose.AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.PROFILE) },
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // AI Orb
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center,
            ) {
                AIOrb(
                    state = VoiceState.IDLE,
                    modifier = Modifier.size(180.dp),
                    onClick = { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.CHAT_NEW) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ask Guide Trade",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W600,
            )

            Text(
                text = "Tap the orb or type your question",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            val quickActions = listOf(
                "Research Market", "Analyze Asset", "Explain Indicator", "Market Overview"
            )

            quickActions.chunked(2).forEach { rowActions ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                ) {
                    rowActions.forEach { action ->
                        Button(
                            onClick = { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.CHAT_NEW) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        ) {
                            Text(
                                text = action,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Research
            Text(
                text = "Recent Research",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            when (val state = homeUiState) {
                is HomeUiState.Success -> {
                    if (state.recentResearch.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            state.recentResearch.take(5).forEach { research ->
                                ResearchCard(
                                    research = research,
                                    onOpen = {
                                        navController.navigate(
                                            com.guidetradeai.ui.navigation.NavRoutes.researchDetailRoute(research.id)
                                        )
                                    },
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No research results yet. Ask the AI a question to get started.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                is HomeUiState.Loading -> {
                    Text("Loading...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                is HomeUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun ResearchCard(
    research: ResearchResult,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = research.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (!research.asset.isNullOrEmpty()) {
                Text(
                    text = research.asset,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = research.response,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = research.createdAt.formatDate("MMM dd, yyyy"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
