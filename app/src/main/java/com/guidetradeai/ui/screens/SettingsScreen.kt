package com.guidetradeai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.SettingsUiState
import com.guidetradeai.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val uiState by settingsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { settingsViewModel.loadSettings() }

    val settings = (uiState as? SettingsUiState.Success)?.settings
    val themeValue = settings?.theme ?: "dark"
    val themeLabel = when (themeValue) {
        "light" -> "Light"
        "dark" -> "Dark"
        "system" -> "System"
        else -> "Dark"
    }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        },
        bottomBar = { BottomBar(navController = navController) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item { SettingsSectionHeader("Preferences") }
            item {
                SettingItem(
                    title = "Voice",
                    icon = Icons.Default.Mic,
                    subtitle = "Voice responses, auto speak",
                ) {
                    navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.VOICE_SETTINGS)
                }
            }
            item {
                SettingItem(
                    title = "Telegram",
                    icon = Icons.Default.Send,
                    subtitle = "Set up Telegram notifications",
                ) {
                    navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.TELEGRAM_SETTINGS)
                }
            }
            item {
                SettingItem(
                    title = "Notifications",
                    icon = Icons.Default.Notifications,
                    subtitle = null,
                ) { }
            }
            item {
                SettingItem(
                    title = "Appearance",
                    icon = Icons.Default.Palette,
                    subtitle = themeLabel,
                ) {
                    showThemeDialog = true
                }
            }
            item { SettingsSectionHeader("Account") }
            item {
                SettingItem(
                    title = "Account",
                    icon = Icons.Default.ManageAccounts,
                    subtitle = null,
                ) {
                    navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.PROFILE)
                }
            }
            item {
                SettingItem(
                    title = "Logout",
                    icon = Icons.Default.ExitToApp,
                    subtitle = null,
                    tint = MaterialTheme.colorScheme.error,
                ) {
                    authViewModel.signOut()
                    navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            item { SettingsSectionHeader("About") }
            item {
                SettingItem(
                    title = "About",
                    icon = Icons.Default.Info,
                    subtitle = null,
                ) {
                    navController.navigate(com.guidetradeai.ui.navigation.NavRoutes.ABOUT)
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentTheme = themeValue,
            onDismiss = { showThemeDialog = false },
            onSelect = { theme ->
                settingsViewModel.updateTheme(theme)
                showThemeDialog = false
            },
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.W600,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        fontSize = 12.sp,
        letterSpacing = 1.sp,
    )
}

@Composable
fun SettingItem(
    title: String,
    icon: ImageVector,
    subtitle: String?,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = tint,
                )
                subtitle?.let { sub ->
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun ThemeSelectorDialog(
    currentTheme: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val themes = listOf(
        "dark" to "Dark",
        "light" to "Light",
        "system" to "System Default",
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme") },
        text = {
            Column {
                themes.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(value) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (value == currentTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                        )
                        if (value == currentTheme) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
