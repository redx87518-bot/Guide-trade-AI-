package com.guidetradeai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.GuideTradeCard
import com.guidetradeai.ui.navigation.NavRoutes
import com.guidetradeai.viewmodel.AuthViewModel

data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>,
)

data class SettingsItem(
    val title: String,
    val route: String? = null,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun SettingsScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val sections = listOf(
        SettingsSection(
            title = "Account",
            items = listOf(
                SettingsItem(title = "Profile", route = NavRoutes.Profile.route),
                SettingsItem(title = "Security", onClick = {}),
            ),
        ),
        SettingsSection(
            title = "Notifications",
            items = listOf(
                SettingsItem(title = "Telegram", route = NavRoutes.TelegramSettings.route),
                SettingsItem(title = "Voice", route = NavRoutes.VoiceSettings.route),
            ),
        ),
        SettingsSection(
            title = "Features",
            items = listOf(
                SettingsItem(title = "Paper Trading", route = NavRoutes.PaperTrading.route),
                SettingsItem(title = "MCP Connectors", route = NavRoutes.McpConnections.route),
            ),
        ),
        SettingsSection(
            title = "App",
            items = listOf(
                SettingsItem(title = "Appearance", onClick = {}),
                SettingsItem(title = "About", onClick = {}),
            ),
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Settings",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            sections.forEach { section ->
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = section.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                        section.items.forEach { item ->
                            GuideTradeCard(
                                onClick = {
                                    if (item.route != null) {
                                        navController.navigate(item.route)
                                    } else if (item.onClick != null) {
                                        item.onClick()
                                    }
                                },
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Medium,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
