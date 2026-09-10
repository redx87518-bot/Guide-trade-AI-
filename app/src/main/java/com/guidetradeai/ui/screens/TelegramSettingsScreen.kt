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
                showMessageDialog = true
                telegramViewModel.clearTestState()
            }
            else -> {}
        }
    }

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
            }
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
                item {
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
                }
                item {
                    OutlinedTextField(
                        value = chatId,
                        onValueChange = { chatId = it },
                        label = { Text("Chat ID") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GuideTradeColors.PrimaryPurple,
                            cursorColor = GuideTradeColors.PrimaryPurple,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    PrimaryButton(
                        text = "Save Settings",
                        onClick = {
                            if (botToken.isNotBlank() && chatId.isNotBlank()) {
                                telegramViewModel.saveSettings(botToken, chatId, true, true, false)
                                promptMessage = "Settings saved successfully!"
                                showMessageDialog = true
                            }
                        },
                    )
                }
                item {
                    SecondaryButton(
                        text = "Test Connection",
                        onClick = {
                            if (botToken.isNotBlank() && chatId.isNotBlank()) {
                                telegramViewModel.testConnection(botToken, chatId)
                            }
                        },
                    )
                }
            }
        }
    }

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
            },
            dismissButton = {
                TextButton(onClick = { showMessageDialog = false }) { Text("Cancel") }
            },
        )
    }
}
