package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AuthViewModel

data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>,
)

data class SettingsItem(
    val title: String,
    val description: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val route: String? = null,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun SettingsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val sections = listOf(
        SettingsSection(
            title = "ACCOUNT",
            items = listOf(
                SettingsItem("Profile", "Account details", Icons.Default.Person, NavRoutes.Profile.route),
                SettingsItem("Security", "Password and authentication", Icons.Default.Security),
            ),
        ),
        SettingsSection(
            title = "NOTIFICATIONS",
            items = listOf(
                SettingsItem("Telegram", "Bot and chat settings", Icons.Default.Notifications, NavRoutes.TelegramSettings.route),
                SettingsItem("Signal Alerts", "Price and signal alerts", Icons.Default.Notifications),
            ),
        ),
        SettingsSection(
            title = "TRADING TOOLS",
            items = listOf(
                SettingsItem("Paper Trading", "Simulated funds only", Icons.Default.ShowChart, NavRoutes.PaperTrading.route),
                SettingsItem("MCP Connectors", "Connect external data", Icons.Default.Settings, NavRoutes.McpConnections.route),
            ),
        ),
        SettingsSection(
            title = "VOICE",
            items = listOf(
                SettingsItem("Voice Assistant", "Speech and TTS settings", Icons.Default.Mic, NavRoutes.VoiceSettings.route),
            ),
        ),
        SettingsSection(
            title = "APP",
            items = listOf(
                SettingsItem("Appearance", "Theme and display", Icons.Default.Brush),
                SettingsItem("About", "Version and credits", Icons.Default.Info),
            ),
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Account and preferences",
                    fontSize = 12.sp,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        sections.forEach { section ->
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = section.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            items(section.items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 3.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
                    onClick = {
                        if (item.route != null) {
                            navController.navigate(item.route)
                        } else if (item.onClick != null) {
                            item.onClick()
                        }
                    },
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.icon != null) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(end = 12.dp),
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            )
                            if (item.description != null) {
                                Text(
                                    text = item.description,
                                    fontSize = 12.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
