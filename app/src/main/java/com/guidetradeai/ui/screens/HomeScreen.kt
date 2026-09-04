package com.guidetradeai.ui.screens

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
import androidx.compose.material3.ButtonDefaults
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
import coil.compose.AsyncImage
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
import androidx.compose.runtime.setValue

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
) {
    val homeViewModel: HomeViewModel = viewModel()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState) {
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
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
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.W700,
                    )
                    Text(
                        text = "Ready to explore the markets?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                val avatarUrl = (authState as? AuthUiState.Authenticated)?.user?.avatarUrl
                if (!avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.PROFILE) },
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center,
            ) {
                AIOrb(
                    state = VoiceState.IDLE,
                    modifier = Modifier.size(160.dp),
                    onClick = { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.CHAT_NEW) },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ask Guide Trade",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W700,
            )

            Text(
                text = "Tap the orb or type your question",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            val quickActions = listOf(
                "Research Market", "Analyze Asset", "Explain Indicator", "Market Overview"
            )

            quickActions.chunked(2).forEach { rowActions ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                ) {
                    rowActions.forEach { action ->
                        Button(
                            onClick = { navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.CHAT_NEW) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 2.dp),
                        ) {
                            Text(
                                text = action,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Recent Research",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.W700,
                )
            }

            when (val state = homeUiState) {
                is HomeUiState.Success -> {
                    if (state.recentResearch.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "No research yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.W600,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ask the AI a question to get started.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = research.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.weight(1f),
                )
                if (!research.asset.isNullOrEmpty()) {
                    Text(
                        text = research.asset,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = research.response,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = research.createdAt.formatDate("MMM dd, yyyy"),
                style = MaterialTheme.typography.labelSmall,
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
