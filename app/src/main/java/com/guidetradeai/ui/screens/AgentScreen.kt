package com.guidetradeai.ui.screens

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.delay
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.navigation.NavHostController

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
            InputBar(
                onSend = { chatViewModel.sendMessage(it) },
                onVoice = {
                    if (isListening) chatViewModel.stopVoiceInput() else chatViewModel.startVoiceInput()
                },
                isListening = isListening,
                isLoading = isLoading,
                modifier = Modifier.align(Alignment.BottomCenter),
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
                        shape = CircleShape,
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "How can I help you understand the market?",
            color = GuideTradeColors.TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
fun InputBar(
    onSend: (String) -> Unit,
    onVoice: () -> Unit,
    isListening: Boolean,
    isLoading: Boolean,
    var text by rememberSaveable { mutableStateOf("") }
    Surface(
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = GuideTradeColors.PrimarySurface,
        tonalElevation = 0.dp,
        Row(
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                maxLines = 4,
                enabled = !isLoading,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = GuideTradeColors.BrightPurple,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
            IconButton(
                onClick = onVoice,
                    .size(44.dp)
                        if (isListening) GuideTradeColors.PrimaryPurple else GuideTradeColors.SecondarySurface,
                        CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice",
                    tint = if (isListening) GuideTradeColors.White else GuideTradeColors.TextSecondary,
                    modifier = Modifier.size(20.dp),
                onClick = {
                    if (text.isNotBlank() && !isLoading) {
                        onSend(text)
                        text = ""
                enabled = text.isNotBlank() && !isLoading,
                    .background(GuideTradeColors.PrimaryPurple, CircleShape),
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = GuideTradeColors.White,
fun UserMessage(message: String) {
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
                .widthIn(max = 280.dp)
                .background(GuideTradeColors.PrimaryPurple, RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            Text(
                text = message,
                color = GuideTradeColors.White,
                fontSize = 14.sp,
                lineHeight = 20.sp,
fun AgentMessage(
    message: String,
    marketData: com.guidetradeai.domain.model.MarketDataResponse?,
        horizontalAlignment = Alignment.Start,
                .background(GuideTradeColors.PrimarySurface, RoundedCornerShape(18.dp))
                .border(1.dp, GuideTradeColors.SubtleBorder, RoundedCornerShape(18.dp))
                .padding(16.dp),
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (marketData != null) {
                    MarketDataCard(marketData = marketData)
                    Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = GuideTradeColors.TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
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
                        text = "NORTH7",
fun MarketDataCard(marketData: com.guidetradeai.domain.model.MarketDataResponse) {
        modifier = Modifier
            .background(GuideTradeColors.CardSurface, RoundedCornerShape(12.dp))
            .border(1.dp, GuideTradeColors.Border, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
                text = "${marketData.symbol.ifBlank { marketData.market }}",
                color = GuideTradeColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            StatusBadge(
                text = marketData.provider.ifBlank { "INTEL" },
                color = GuideTradeColors.BrightPurple,
        if (marketData.price != null) {
            PriceText(value = marketData.price, fontSize = 16f)
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            if (marketData.changePercent != null) {
                ChangeText(change = marketData.change, changePercent = marketData.changePercent)
            if (marketData.signal.isNotBlank()) {
                StatusBadge(
                    text = marketData.signal,
                    color = when (marketData.signal.lowercase()) {
                        "bullish", "buy" -> GuideTradeColors.Positive
                        "bearish", "sell" -> GuideTradeColors.Negative
                        else -> GuideTradeColors.Warning
                    },
        if (marketData.news.isNotEmpty()) {
            marketData.news.firstOrNull()?.let { news ->
                    text = news.title,
                    color = GuideTradeColors.TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
fun ThinkingIndicator() {
    Row(
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
                .size(8.dp)
                .background(GuideTradeColors.BrightPurple, CircleShape),
                .background(GuideTradeColors.BrightPurple.copy(alpha = 0.6f), CircleShape),
                .background(GuideTradeColors.BrightPurple.copy(alpha = 0.3f), CircleShape),
        Spacer(modifier = Modifier.width(8.dp))
            text = "GuideTrade is analyzing...",
            color = GuideTradeColors.MutedText,
            fontSize = 12.sp,