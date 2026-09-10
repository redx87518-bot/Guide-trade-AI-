package com.guidetradeai.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.icons.filled.Mic
import androidx.compose.material3.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.viewmodel.SettingsUiState
import com.guidetradeai.viewmodel.SettingsViewModel

@Composable
fun VoiceSettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { settingsViewModel.loadSettings() }

    val settings = (uiState as? SettingsUiState.Success)?.settings
    var voiceEnabled by remember { mutableStateOf(settings?.voiceEnabled ?: true) }
    var autoSpeak by remember { mutableStateOf(settings?.autoSpeak ?: false) }

    var localVoice by remember { mutableStateOf("en-US-N") }
    var speed by remember { mutableStateOf(1.0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        bottomBar = { BottomBar(navController = navController) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingToggleRow(
                title = "Voice Responses",
                subtitle = "Enable spoken AI responses",
                checked = voiceEnabled,
                onCheckedChange = { enabled ->
                    voiceEnabled = enabled
                    settingsViewModel.updateVoiceEnabled(enabled)
                },
            )

            SettingToggleRow(
                title = "Auto Speak",
                subtitle = "Automatically play AI responses",
                checked = autoSpeak,
                onCheckedChange = { enabled ->
                    autoSpeak = enabled
                    settingsViewModel.updateAutoSpeak(enabled)
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Voice Profile",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 16.dp, top = 8.dp),
                fontSize = 12.sp,
                letterSpacing = 1.sp,
            )

            SettingToggleRow(
                title = "Voice Selection",
                subtitle = localVoice,
                checked = false,
                onCheckedChange = { },
            )

            SettingToggleRow(
                title = "Playback Speed",
                subtitle = String.format("%.1fx", speed),
                checked = false,
                onCheckedChange = { },
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange(!checked) },
            colors = SwitchDefaults.colors(
                checkedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}
