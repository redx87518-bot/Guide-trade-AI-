package com.guidetradeai.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.ui.theme.AccentCyan
import com.guidetradeai.ui.theme.AccentPurple
import com.guidetradeai.ui.theme.Background
import com.guidetradeai.ui.theme.DividerColor
import com.guidetradeai.ui.theme.ErrorColor
import com.guidetradeai.ui.theme.SurfaceDark
import com.guidetradeai.ui.theme.TextPrimary
import com.guidetradeai.ui.theme.SurfaceMid
import com.guidetradeai.ui.theme.TextSecondary
import com.guidetradeai.viewmodel.AuthViewModel
import com.guidetradeai.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val user = authViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) { settingsViewModel.loadSettings() }

    val settings = (uiState as? com.guidetradeai.viewmodel.SettingsUiState.Success)?.settings
    var showThemeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }

    val themeValue = settings?.theme ?: "dark"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceDark,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceDark,
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(56.dp),
                                shape = CircleShape,
                                color = AccentCyan.copy(alpha = 0.15f),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = user.value?.fullName?.firstOrNull()?.toString()?.uppercase() ?: "U",
                                        color = AccentCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = user.value?.fullName ?: "User",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                )
                                Text(
                                    text = user.value?.email ?: "",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                SettingsCard(title = "ASSISTANT") {
                    SettingsRow(
                        icon = Icons.Default.Mic,
                        title = "Voice responses",
                        trailing = {
                            Switch(
                                checked = settings?.voiceEnabled ?: true,
                                onCheckedChange = {
                                    settingsViewModel.updateVoiceEnabled(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AccentCyan,
                                    checkedTrackColor = AccentCyan.copy(alpha = 0.3f),
                                ),
                            )
                        },
                    )
                    Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                    SettingsRow(
                        icon = Icons.Default.Mic,
                        title = "Auto-speak replies",
                        trailing = {
                            Switch(
                                checked = settings?.autoSpeak ?: false,
                                onCheckedChange = {
                                    settingsViewModel.updateAutoSpeak(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AccentCyan,
                                    checkedTrackColor = AccentCyan.copy(alpha = 0.3f),
                                ),
                            )
                        },
                    )
                    Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                    SettingsRow(
                        icon = Icons.Default.Mic,
                        title = "Voice model",
                        trailing = {
                            Text(
                                text = "Quan Voice (ElevenLabs)",
                                color = TextSecondary,
                                fontSize = 13.sp,
                            )
                        },
                    )
                }
            }

            item {
                SettingsCard(title = "INTERFACE") {
                    SettingsRow(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        trailing = {
                            Surface(
                                modifier = Modifier.clickable { showThemeDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceMid,
                            ) {
                                Text(
                                    text = when (themeValue) {
                                        "light" -> "Light"
                                        "system" -> "System"
                                        else -> "Dark"
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                )
                            }
                        },
                    )
                }
            }

            item {
                SettingsCard(title = "ACCOUNT") {
                    SettingsRow(
                        icon = Icons.Default.Palette,
                        title = "Full name",
                        trailing = {
                            Text(
                                text = user.value?.fullName ?: "",
                                color = TextSecondary,
                                fontSize = 13.sp,
                            )
                        },
                    )
                    Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                    SettingsRow(
                        icon = Icons.Default.Palette,
                        title = "Email",
                        trailing = {
                            Text(
                                text = user.value?.email ?: "",
                                color = TextSecondary,
                                fontSize = 13.sp,
                            )
                        },
                    )
                    Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                    SettingsRow(
                        icon = Icons.Default.ArrowBack,
                        title = "Sign Out",
                        trailing = null,
                        titleColor = ErrorColor,
                        onClick = { showSignOutDialog = true },
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                ) {
                    SettingsRow(
                        icon = Icons.Default.ArrowBack,
                        title = "Delete Account",
                        trailing = null,
                        titleColor = ErrorColor,
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    )
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectorDialog(
            currentTheme = themeValue,
            onDismiss = { showThemeDialog = false },
            onSelect = {
                settingsViewModel.updateTheme(it)
                showThemeDialog = false
            },
        )
    }

    if (showSignOutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Sign Out", color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This action cannot be undone. All your data will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
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
    BottomBar(navController = navController)
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = AccentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.W500,
                letterSpacing = 0.08.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )
            content()
        }
    }
    BottomBar(navController = navController)
}

@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    trailing: @Composable (() -> Unit)? = null,
    titleColor: Color = TextPrimary,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = titleColor,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Box(modifier = Modifier.padding(start = 8.dp)) {
                trailing()
            }
        }
    }
    BottomBar(navController = navController)
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
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme") },
        text = {
            Column {
                themes.forEach { (value, label) ->
                    val selected = value == currentTheme
                    val animatedScale by animateFloatAsState(
                        targetValue = if (selected) 1.02f else 1f,
                        label = "scale",
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(animatedScale)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(value) },
                        color = if (selected) AccentCyan.copy(alpha = 0.1f) else Color.Transparent,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = label,
                                color = if (selected) AccentCyan else TextPrimary,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f),
                            )
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = SurfaceDark,
    )
    BottomBar(navController = navController)
}
