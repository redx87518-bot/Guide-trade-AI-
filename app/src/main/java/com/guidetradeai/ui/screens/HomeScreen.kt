package com.guidetradeai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Briefcase
import androidx.compose.material.icons.filled.ChartArea
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Sparkles
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.guidetradeai.ui.components.*
import com.guidetradeai.ui.theme.GuideTradeColors
import com.guidetradeai.voice.VoiceState
import com.guidetradeai.viewmodel.*
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.*
import com.guidetradeai.audio.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import android.util.Log
import android.widget.Toast
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.delay
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Bundle
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Base64
import java.io.File
import java.util.Locale
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { homeViewModel.loadHome() }

    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "GUIDETRADE AI",
                subtitle = "Market intelligence, simplified.",
                navigationIcon = null,
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = GuideTradeColors.TextPrimary)
                    }
                },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        val listState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeroCard(
                    onAskGuideTrade = {
                        navController.navigate(NavRoutes.AGENT)
                    },
                    onAnalyzeAsset = {
                        navController.navigate(NavRoutes.MARKETS)
                    },
                )
            }

            item {
                SectionHeader(
                    title = "Market Overview",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InfoCard(
                        title = "Regime",
                        value = "Neutral",
                        modifier = Modifier.weight(1f),
                    )
                    InfoCard(
                        title = "Risk",
                        value = "Moderate",
                        modifier = Modifier.weight(1f),
                        tint = GuideTradeColors.Warning,
                    )
                    InfoCard(
                        title = "Status",
                        value = "Active",
                        modifier = Modifier.weight(1f),
                        tint = GuideTradeColors.Positive,
                    )
                }
            }

            item { Divider(modifier = Modifier.padding(horizontal = 20.dp), color = GuideTradeColors.SubtleBorder) }

            item {
                SectionHeader(
                    title = "Latest Intelligence",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }

            items(listOf(
                "Latest Signal" to "BTC/USD",
                "Market Risk" to "Moderate",
                "Major Event" to "Fed Rate Decision",
                "Market Regime" to "Neutral",
            )) { (title, value) ->
                GuideTradeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    onClick = { /* TODO */ },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                color = GuideTradeColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Just now",
                                color = GuideTradeColors.MutedText,
                                fontSize = 11.sp,
                            )
                        }
                        Text(
                            text = value,
                            color = GuideTradeColors.BrightPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }

            item { Divider(modifier = Modifier.padding(horizontal = 20.dp), color = GuideTradeColors.SubtleBorder) }

            item {
                SectionHeader(
                    title = "Watchlist",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    action = {
                        TextButton(onClick = { /* TODO */ }) {
                            Text(text = "Add", color = GuideTradeColors.BrightPurple, fontSize = 12.sp)
                        }
                    },
                )
            }

            items(listOf(
                Triple("BTC", "Bitcoin", 67421.32 to 2.34),
                Triple("ETH", "Ethereum", 3456.78 to -1.23),
                Triple("AAPL", "Apple Inc.", 189.55 to 0.87),
            )) { (symbol, name, data) ->
                val parts = data.toString().split(" to ")
                val price = parts[0].toDoubleOrNull()
                val change = parts.getOrNull(1)?.toDoubleOrNull()
                AssetRow(
                    symbol = symbol,
                    name = name,
                    price = price,
                    changePercent = change,
                    onClick = { navController.navigate(NavRoutes.assetDetailRoute(symbol)) },
                )
            }
        }
    }
}

@Composable
fun HeroCard(
    onAskGuideTrade: () -> Unit,
    onAnalyzeAsset: () -> Unit,
) {
    GuideTradeCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        containerColor = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GuideTradeColors.PrimaryPurple.copy(alpha = 0.25f),
                            GuideTradeColors.PrimarySurface.copy(alpha = 0.9f),
                            GuideTradeColors.PrimarySurface,
                        ),
                        center = Alignment.TopCenter,
                        radius = 500f,
                    ),
                    shape = RoundedCornerShape(20.dp),
                )
                .border(1.dp, GuideTradeColors.SubtleBorder, RoundedCornerShape(20.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Good evening.",
                        color = GuideTradeColors.TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What's happening in the markets?",
                        color = GuideTradeColors.TextSecondary,
                        fontSize = 15.sp,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PrimaryButton(
                        text = "Ask GuideTrade",
                        onClick = onAskGuideTrade,
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Sparkles,
                    )
                    SecondaryButton(
                        text = "Analyze an asset",
                        onClick = onAnalyzeAsset,
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Analytics,
                    )
                }
            }
        }
    }
}
