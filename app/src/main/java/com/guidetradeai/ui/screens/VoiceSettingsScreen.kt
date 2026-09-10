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
fun VoiceSettingsScreen(navController: NavHostController) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val uiState by settingsViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            GuideTradeTopBar(
                title = "Voice Settings",
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
                GuideTradeCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Voice Responses", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Enable voice output", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                        }
                        androidx.compose.material3.Switch(
                            checked = (uiState as? SettingsUiState.Success)?.settings?.voiceEnabled ?: true,
                            onCheckedChange = { settingsViewModel.updateVoiceEnabled(it) },
                        )
                    }
                }
            }
                            Text(text = "Auto-play Responses", color = GuideTradeColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Automatically speak AI responses", color = GuideTradeColors.TextSecondary, fontSize = 12.sp)
                            checked = (uiState as? SettingsUiState.Success)?.settings?.autoSpeak ?: false,
                            onCheckedChange = { settingsViewModel.updateAutoSpeak(it) },
        }
    }
}