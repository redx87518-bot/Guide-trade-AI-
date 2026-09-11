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
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.ShowChart
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
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.TextFieldDefaults
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
import com.guidetradeai.viewModel.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

@Composable
fun AgentScreen(
    navController: NavHostController,
    chatViewModel: ChatViewModel = viewModel(),
) {
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val isListening by chatViewModel.isListening.collectAsState()
    val isSpeaking by chatViewModel.isSpeaking.collectAsState()
    val error by chatViewModel.error.collectAsState()
    val currentSessionTitle by chatViewModel.currentSessionTitle.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { chatViewModel.initialize() }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(error) {
        error?.let {
            // Show error toast/snackbar
            kotlinx.coroutines.delay(3000)
            chatViewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "GUIDETRADE AGENT",
                subtitle = "Market intelligence online",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navController.popBackStack() },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (messages.isEmpty() && !isLoading) {
                    EmptyChatState(
                        onPromptClick = { chatViewModel.sendMessage(it) },
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            if (msg.role == "user") {
                                UserMessage(message = msg.content)
                            } else {
                                AgentMessage(
                                    message = msg.content,
                                    marketData = msg.marketData,
                                    isLoading = false,
                                )
                            }
                        }
                        if (isLoading) {
                            item {
                                ThinkingIndicator()
                            }
                        }
                    }
                }
            }

            InputBar(
                onSend = { chatViewModel.sendMessage(it) },
                onVoice = {
                    if (isListening) chatViewModel.stopVoiceInput() else chatViewModel.startVoiceInput()
                },
                isListening = isListening,
                isLoading = isLoading,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
fun EmptyChatState(
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GuideTradeColors.PrimaryPurple.copy(alpha = 0.3f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GuideTradeColors.BrightPurple,
                                GuideTradeColors.PrimaryPurple,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "How can I help you understand the market?",
            color = GuideTradeColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            listOf(
                "Analyze BTC",
                "What is the current market risk?",
                "What's moving the market?",
                "Show me recent market events",
                "Analyze gold",
                "Review my paper portfolio",
            ).forEach { prompt ->
                GhostButton(
                    text = prompt,
                    onClick = { onPromptClick(prompt) },
                )
            }
        }
    }
}

@Composable
fun InputBar(
    onSend: (String) -> Unit,
    onVoice: () -> Unit,
    isListening: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = GuideTradeColors.PrimarySurface,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about markets...", color = GuideTradeColors.MutedText) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GuideTradeColors.PrimaryPurple,
                    unfocusedBorderColor = GuideTradeColors.Border,
                    cursorColor = GuideTradeColors.PrimaryPurple,
                    focusedTextColor = GuideTradeColors.TextPrimary,
                    unfocusedTextColor = GuideTradeColors.TextPrimary,
                ),
                maxLines = 4,
                enabled = !isLoading,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = GuideTradeColors.BrightPurple,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                },
            )
            IconButton(
                onClick = onVoice,
                enabled = !isLoading,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isListening) GuideTradeColors.PrimaryPurple else GuideTradeColors.SecondarySurface,
                        CircleShape,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = if (isListening) GuideTradeColors.White else GuideTradeColors.TextSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            IconButton(
                onClick = {
                    if (text.isNotBlank() && !isLoading) {
                        onSend(text)
                        text = ""
                    }
                },
                enabled = text.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(44.dp)
                    .background(GuideTradeColors.PrimaryPurple, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = GuideTradeColors.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun UserMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(GuideTradeColors.PrimaryPurple, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = message,
                color = GuideTradeColors.White,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
fun AgentMessage(
    message: String,
    marketData: com.guidetradeai.domain.model.MarketDataResponse?,
    isLoading: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GuideTradeColors.PrimarySurface, RoundedCornerShape(18.dp))
                .border(1.dp, GuideTradeColors.SubtleBorder, RoundedCornerShape(18.dp))
                .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (marketData != null) {
                    MarketDataCard(marketData = marketData)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = message,
                    color = GuideTradeColors.TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "GuideTrade Agent",
                        color = GuideTradeColors.MutedText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "NORTH7",
                        color = GuideTradeColors.MutedText,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun MarketDataCard(marketData: com.guidetradeai.domain.model.MarketDataResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GuideTradeColors.CardSurface, RoundedCornerShape(12.dp))
            .border(1.dp, GuideTradeColors.Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${marketData.symbol.ifBlank { marketData.market }}",
                color = GuideTradeColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            StatusBadge(
                text = marketData.provider.ifBlank { "INTEL" },
                color = GuideTradeColors.BrightPurple,
            )
        }
        if (marketData.price != null) {
            PriceText(value = marketData.price, fontSize = 16f)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (marketData.changePercent != null) {
                ChangeText(change = marketData.change, changePercent = marketData.changePercent)
            }
            if (marketData.signal.isNotBlank()) {
                StatusBadge(
                    text = marketData.signal,
                    color = when (marketData.signal.lowercase()) {
                        "bullish", "buy" -> GuideTradeColors.Positive
                        "bearish", "sell" -> GuideTradeColors.Negative
                        else -> GuideTradeColors.Warning
                    },
                )
            }
        }
        if (marketData.news.isNotEmpty()) {
            marketData.news.firstOrNull()?.let { news ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.title,
                    color = GuideTradeColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun ThinkingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(GuideTradeColors.BrightPurple, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(GuideTradeColors.BrightPurple.copy(alpha = 0.6f), CircleShape),
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(GuideTradeColors.BrightPurple.copy(alpha = 0.3f), CircleShape),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "GuideTrade is analyzing...",
            color = GuideTradeColors.MutedText,
            fontSize = 12.sp,
        )
    }
}
