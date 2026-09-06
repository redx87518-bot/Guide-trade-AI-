package com.guidetradeai.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.MarkdownText
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.ui.theme.AccentCyan
import com.guidetradeai.ui.theme.AccentPurple
import com.guidetradeai.ui.theme.AccentGlow
import com.guidetradeai.ui.theme.AiBubble
import com.guidetradeai.ui.theme.Background
import com.guidetradeai.ui.theme.DividerColor
import com.guidetradeai.ui.theme.ErrorColor
import com.guidetradeai.ui.theme.SurfaceDark
import com.guidetradeai.ui.theme.SurfaceMid
import com.guidetradeai.ui.theme.TextPrimary
import com.guidetradeai.ui.theme.TextSecondary
import com.guidetradeai.ui.theme.UserBubble
import com.guidetradeai.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatScreen(
    navController: NavHostController,
    sessionId: String? = null,
    chatViewModel: ChatViewModel = viewModel(),
) {
    val messages by chatViewModel.messages.collectAsState()
    val sessions by chatViewModel.sessions.collectAsState()
    val currentSessionId by chatViewModel.currentSessionId.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val isSpeaking by chatViewModel.isSpeaking.collectAsState()
    val isListening by chatViewModel.isListening.collectAsState()
    val error by chatViewModel.error.collectAsState()
    val currentTitle by chatViewModel.currentSessionTitle.collectAsState()

    var messageText by rememberSaveable { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var sessionToDelete by remember { mutableStateOf<String?>(null) }
    var selectedProvider by rememberSaveable { mutableStateOf("StockUp") }
    var selectedFeature by rememberSaveable { mutableStateOf("Chat") }
    var selectedMarket by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedSymbol by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTimeframe by rememberSaveable { mutableStateOf("1h") }
    var showModelSheet by remember { mutableStateOf(false) }
    var showFeatureSheet by remember { mutableStateOf(false) }
    var showMarketSheet by remember { mutableStateOf(false) }
    var showAssetSheet by remember { mutableStateOf(false) }
    var showTimeframeSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val drawerState = remember { DrawerState(DrawerValue.Closed) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    LaunchedEffect(Unit) {
        chatViewModel.initialize()
    }

    LaunchedEffect(currentSessionId) {
        if (currentSessionId == null) {
            chatViewModel.startNewSession()
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            delay(4000)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier.width(300.dp),
                color = SurfaceDark,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        text = "QUAN",
                        style = MaterialTheme.typography.titleLarge,
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                    Text(
                        text = "CONVERSATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            chatViewModel.startNewSession()
                            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                    drawerState.close()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("New Chat", color = AccentCyan)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = DividerColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(sessions, key = { it.id }) { session ->
                            val isActive = session.id == currentSessionId
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        chatViewModel.switchSession(session)
                                        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                    drawerState.close()
                                }
                                    }
                                    .background(
                                        if (isActive) AccentGlow else Color.Transparent
                                    ),
                                color = Color.Transparent,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (isActive) {
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(24.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(AccentCyan),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.title.ifBlank { "New Chat" },
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                        )
                                        Text(
                                            text = formatDate(session.createdAt),
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Delete",
                                        tint = ErrorColor,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                sessionToDelete = session.id
                                                showDeleteDialog = true
                                            },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceDark,
                shadowElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextPrimary)
                    }
                    Text(
                        text = currentTitle.ifBlank { "New Chat" },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = { chatViewModel.startNewSession() }) {
                        Icon(Icons.Default.Add, contentDescription = "New Chat", tint = AccentCyan)
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (messages.isEmpty() && !isLoading) {
                    EmptyChatState(
                        onSuggestionClick = { chatViewModel.sendMessage(it) }
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(messages, key = { it.id }) { message ->
                            if (message.role == "user") {
                                UserMessageBubble(
                                    content = message.content,
                                    timestamp = message.createdAt,
                                )
                            } else {
                                AiMessageBubble(
                                    content = message.content,
                                    timestamp = message.createdAt,
                                )
                            }
                        }
                        if (isLoading) {
                            item {
                                TypingIndicator()
                            }
                        }
                    }
                }
            }

            val currentError = error
            if (currentError != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = ErrorColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = currentError,
                        color = ErrorColor,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                    )
                }
            }

            if (showModelSheet) {
                ModelSelectionBottomSheet(
                    selectedProvider = selectedProvider,
                    onProviderSelected = { 
                        selectedProvider = it
                        selectedFeature = when (it) {
                            "SiftingIO" -> "Full Analysis"
                            "Guavy" -> "Full Analysis"
                            "Combined" -> "Full Analysis"
                            else -> "Chat"
                        }
                        selectedMarket = null
                        selectedSymbol = null
                    },
                    onDismiss = { showModelSheet = false },
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Background,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = SurfaceMid,
                        onClick = { showModelSheet = true },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Model",
                                tint = AccentCyan,
                            )
                        }
                    }
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text("Ask Quan...", color = TextSecondary)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceMid,
                            unfocusedContainerColor = SurfaceMid,
                            focusedIndicatorColor = AccentCyan,
                            unfocusedIndicatorColor = DividerColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentCyan,
                        ),
                        shape = RoundedCornerShape(28.dp),
                        maxLines = 4,
                    )
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = if (messageText.isNotBlank() && !isLoading) AccentCyan else DividerColor,
                        onClick = {
                            if (messageText.isNotBlank() && !isLoading) {
                                chatViewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (messageText.isNotBlank() && isLoading) TextSecondary else Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
    if (selectedProvider != "StockUp") {
        DynamicProviderControls(
            provider = selectedProvider,
            feature = selectedFeature,
            market = selectedMarket,
            symbol = selectedSymbol,
            timeframe = selectedTimeframe,
            onFeatureChange = { selectedFeature = it },
            onMarketChange = { selectedMarket = it },
            onSymbolChange = { selectedSymbol = it },
            onTimeframeChange = { selectedTimeframe = it },
        )
    }

    BottomBar(navController = navController)

    if (showDeleteDialog && sessionToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Chat?") },
            text = { Text("This will permanently delete this conversation and all its messages.") },
            confirmButton = {
                TextButton(onClick = {
                    chatViewModel.deleteSession(sessionToDelete!!)
                    showDeleteDialog = false
                    sessionToDelete = null
                }) {
                    Text("Delete", color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
fun EmptyChatState(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    spotColor = AccentCyan.copy(alpha = 0.4f),
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan.copy(alpha = 0.2f), Color.Transparent),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AccentCyan, AccentPurple),
                        ),
                    ),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ask Quan anything",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SuggestionChip(
                text = "Market overview today",
                modifier = Modifier.weight(1f),
                onClick = { onSuggestionClick("Market overview today") },
            )
            SuggestionChip(
                text = "Analyse BTC/USD",
                modifier = Modifier.weight(1f),
                onClick = { onSuggestionClick("Analyse BTC/USD") },
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        SuggestionChip(
            text = "Risk management tips",
            modifier = Modifier.fillMaxWidth(),
            onClick = { onSuggestionClick("Risk management tips") },
        )
    }
}

@Composable
fun SuggestionChip(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = SurfaceMid,
        shape = RoundedCornerShape(20.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = AccentCyan,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun UserMessageBubble(content: String, timestamp: String) {
    val formattedTime = remember(timestamp) {
        try {
            val instant = Instant.parse(timestamp)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter)
        } catch (e: Exception) {
            ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = UserBubble.copy(alpha = 0.5f),
                    ),
                color = UserBubble,
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    text = content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                )
            }
            if (formattedTime.isNotBlank()) {
                Text(
                    text = formattedTime,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                )
            }
        }
    }
}

@Composable
fun AiMessageBubble(content: String, timestamp: String) {
    val formattedTime = remember(timestamp) {
        try {
            val instant = Instant.parse(timestamp)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter)
        } catch (e: Exception) {
            ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(top = 4.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = AccentCyan.copy(alpha = 0.3f),
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan, AccentPurple),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Q",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.widthIn(max = 280.dp),
        ) {
            Surface(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = AiBubble.copy(alpha = 0.5f),
                    ),
                color = AiBubble,
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    MarkdownText(
                        text = content,
                        color = TextPrimary,
                    )
                }
            }
            if (formattedTime.isNotBlank()) {
                Text(
                    text = formattedTime,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .padding(top = 4.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = AccentCyan.copy(alpha = 0.3f),
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan, AccentPurple),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Q",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            modifier = Modifier.padding(top = 4.dp),
            color = AiBubble,
            shape = RoundedCornerShape(20.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) { index ->
                    val delay = index * 150
                    val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = -8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, delayMillis = delay, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse,
                        ),
                        label = "dot_offset",
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = offsetY.dp)
                            .clip(CircleShape)
                            .background(AccentCyan),
                    )
                }
            }
        }
    }
}

fun formatDate(iso: String): String {
    return try {
        val instant = Instant.parse(iso)
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDateTime.now().toLocalDate()
        when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> date.format(formatter)
        }
    } catch (e: Exception) {
        ""
    }
}


@Composable
fun ModelSelectionBottomSheet(
    selectedProvider: String,
    onProviderSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val models = listOf("StockUp", "SiftingIO", "Guavy", "Combined")
    val bottomSheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = SurfaceDark,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "SELECT MODEL",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            models.forEach { model ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { 
                            onProviderSelected(model)
                            onDismiss()
                        },
                    color = if (model == selectedProvider) AccentCyan.copy(alpha = 0.15f) else SurfaceMid,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = model,
                            color = if (model == selectedProvider) AccentCyan else TextPrimary,
                            fontWeight = if (model == selectedProvider) FontWeight.Bold else FontWeight.Normal,
                        )
                        if (model == selectedProvider) {
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DynamicProviderControls(
    provider: String,
    feature: String,
    market: String?,
    symbol: String?,
    timeframe: String,
    onFeatureChange: (String) -> Unit,
    onMarketChange: (String) -> Unit,
    onSymbolChange: (String) -> Unit,
    onTimeframeChange: (String) -> Unit,
) {
    val markets = listOf("Crypto", "Forex", "Commodities", "Stocks")
    val timeframes = listOf("1m", "5m", "15m", "30m", "1h", "1d", "1w", "1mo")
    
    val features = when (provider) {
        "SiftingIO" -> listOf("Full Analysis", "Technical Signal", "Signal History", "Live Price", "RSI", "MACD", "Market Status")
        "Guavy" -> listOf("Full Analysis", "Instrument Analysis", "Scorecard", "Sentiment", "Technical Indicators", "Price History", "News", "Current Action", "Current Trend", "Market Summary")
        "Combined" -> listOf("Full Analysis", "Research", "Technical Signal", "Sentiment")
        else -> emptyList()
    }

    if (features.isEmpty()) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SimpleDropdown(
                    label = "Feature",
                    options = features,
                    selectedOption = feature,
                    onOptionSelected = onFeatureChange,
                    modifier = Modifier.weight(1f),
                )
                SimpleDropdown(
                    label = "Market",
                    options = markets,
                    selectedOption = market ?: "Market",
                    onOptionSelected = onMarketChange,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SimpleDropdown(
                    label = "Asset",
                    options = listOf(symbol ?: "Search..."),
                    selectedOption = symbol ?: "Asset",
                    onOptionSelected = onSymbolChange,
                    modifier = Modifier.weight(1f),
                    enabled = market != null,
                )
                SimpleDropdown(
                    label = "Timeframe",
                    options = timeframes,
                    selectedOption = timeframe,
                    onOptionSelected = onTimeframeChange,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun SimpleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            onClick = { if (enabled) expanded = true },
            shape = RoundedCornerShape(8.dp),
            color = if (enabled) SurfaceMid else SurfaceMid.copy(alpha = 0.5f),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = selectedOption.ifBlank { label },
                    color = if (enabled) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 14.sp) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
