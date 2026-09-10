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

@Composable
fun SettingsScreen(navController: NavHostController) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val uiState by settingsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Settings",
                navigationIcon = null,
            )
        },
        bottomBar = { GuideTradeBottomBar(navController = navController) },
        containerColor = GuideTradeColors.Background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                SectionHeader(title = "Account")
            }
            item {
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.PROFILE) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GuideTradeColors.BrightPurple, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Profile", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                Text(text = "Manage your account", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
            item { Divider(color = GuideTradeColors.SubtleBorder) }
            item {
                SectionHeader(title = "Appearance")
            }
            item {
                var selectedTheme by rememberSaveable { mutableStateOf("dark") }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Light", "Dark", "System").forEach { theme ->
                        val isSelected = selectedTheme == theme.lowercase()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isSelected) GuideTradeColors.PrimaryPurple else GuideTradeColors.SecondarySurface, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedTheme = theme.lowercase()
                                    settingsViewModel.updateTheme(theme.lowercase())
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = theme, color = if (isSelected) GuideTradeColors.White else GuideTradeColors.TextSecondary, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    }
                }
            }
            item { Divider(color = GuideTradeColors.SubtleBorder) }
            item {
                SectionHeader(title = "AI")
            }
            item {
                GuideTradeCard(onClick = { /* TODO */ }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "GuideTrade Agent", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Primary intelligence experience", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        StatusBadge(text = "ACTIVE", color = GuideTradeColors.Positive)
                    }
                }
            }
            item {
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.VOICE_SETTINGS) }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Voice", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Voice responses and input", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
            item { Divider(color = GuideTradeColors.SubtleBorder) }
            item {
                SectionHeader(title = "Connections")
            }
            item {
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.TELEGRAM_SETTINGS) }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Telegram", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Connect Telegram bot", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
            item {
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.MCP_CONNECTIONS) }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "MCP Connections", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Advanced integrations", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
            item { Divider(color = GuideTradeColors.SubtleBorder) }
            item {
                GuideTradeCard(onClick = { navController.navigate(NavRoutes.ABOUT) }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "About", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Version, privacy, terms", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = GuideTradeColors.MutedText, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
