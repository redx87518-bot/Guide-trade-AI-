package com.guidetradeai.ui.screens

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

package com.guidetradeai.ui.screens

@Composable
fun TelegramSettingsScreen(navController: NavHostController) {
    val telegramViewModel: TelegramViewModel = viewModel()
    val uiState by telegramViewModel.uiState.collectAsState()
    val testState by telegramViewModel.testState.collectAsState()
    LaunchedEffect(Unit) { telegramViewModel.loadSettings() }
    var botToken by rememberSaveable { mutableStateOf("") }
    var chatId by rememberSaveable { mutableStateOf("") }
    var showToken by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var promptMessage by remember { mutableStateOf("") }
    var isConfigured by remember { mutableStateOf(false) }
    val settings = (uiState as? TelegramUiState.Success)?.settings
    if (settings != null) {
        LaunchedEffect(settings) {
            isConfigured = settings.enabled && settings.chatId != null
        }
    }
    LaunchedEffect(testState) {
        when (testState) {
            is TelegramTestState.Success -> {
                promptMessage = (testState as? TelegramTestState.Success)?.message ?: "Connection successful!"
                showMessageDialog = true
                telegramViewModel.clearTestState()
            }
            is TelegramTestState.Error -> {
                promptMessage = (testState as? TelegramTestState.Error)?.message ?: "Connection failed"
            else -> {}
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Telegram",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { navController.popBackStack() },
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(text = "Connect your Telegram bot to receive market intelligence updates.", color = GuideTradeColors.TextSecondary, fontSize = 14.sp)
            if (isConfigured) {
                item {
                    GuideTradeCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Connected", color = GuideTradeColors.Positive, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "Chat ID: ${settings?.chatId}", color = GuideTradeColors.TextSecondary, fontSize = 13.sp)
                            Text(text = telegramViewModel.maskToken(settings?.botTokenEncrypted), color = GuideTradeColors.TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                    OutlinedTextField(
                        value = botToken,
                        onValueChange = { botToken = it },
                        label = { Text("Bot Token") },
                        singleLine = true,
                        visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GuideTradeColors.PrimaryPurple,
                            cursorColor = GuideTradeColors.PrimaryPurple,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                        value = chatId,
                        onValueChange = { chatId = it },
                        label = { Text("Chat ID") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    PrimaryButton(
                        text = "Save Settings",
                        onClick = {
                            if (botToken.isNotBlank() && chatId.isNotBlank()) {
                                telegramViewModel.saveSettings(botToken, chatId, true, true, false)
                                promptMessage = "Settings saved successfully!"
                                showMessageDialog = true
                            }
                        },
                    SecondaryButton(
                        text = "Test Connection",
                                telegramViewModel.testConnection(botToken, chatId)
    if (showMessageDialog) {
        AlertDialog(
            onDismissRequest = { showMessageDialog = false },
            title = {
                Text(
                    text = if (testState is TelegramTestState.Error) "Error" else "Success",
                    color = if (testState is TelegramTestState.Error) GuideTradeColors.Negative else GuideTradeColors.Positive,
                )
            },
            text = { Text(promptMessage) },
            confirmButton = {
                TextButton(onClick = { showMessageDialog = false }) { Text("OK") }
            dismissButton = {
                TextButton(onClick = { showMessageDialog = false }) { Text("Cancel") }
        )
}