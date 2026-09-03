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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.icons.filled.Visibility
import androidx.compose.material3.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.guidetradeai.ui.components.BottomBar
import com.guidetradeai.viewmodel.TelegramTestState
import com.guidetradeai.viewmodel.TelegramUiState
import com.guidetradeai.viewmodel.TelegramViewModel

@Composable
fun TelegramSettingsScreen(
    navController: NavHostController,
    telegramViewModel: TelegramViewModel = viewModel(),
) {
    val uiState by telegramViewModel.uiState.collectAsState()
    val testState by telegramViewModel.testState.collectAsState()

    LaunchedEffect(Unit) { telegramViewModel.loadSettings() }

    val settings = (uiState as? TelegramUiState.Success)?.settings
    var botToken by rememberSaveable { mutableStateOf("") }
    var chatId by rememberSaveable { mutableStateOf("") }
    var showToken by remember { mutableStateOf(false) }

    val isConfigured = settings?.enabled == true && settings?.chatId != null
    val maskedToken = telegramViewModel.maskToken(settings?.botTokenEncrypted)

    var showMessageDialog by remember { mutableStateOf(false) }
    var promptMessage by remember { mutableStateOf("") }

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

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telegram Settings") },
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
                .padding(padding)
                .padding(24.dp),
        ) {
            if (isConfigured && maskedToken.isNotEmpty()) {
                Text(
                    text = maskedToken,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),
                )
            } else {
                OutlinedTextField(
                    value = botToken,
                    onValueChange = { botToken = it },
                    label = { Text("Bot Token") },
                    singleLine = true,
                    visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showToken = !showToken }) {
                            Icon(
                                imageVector = if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showToken) "Hide" else "Show",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = chatId,
                    onValueChange = { chatId = it },
                    label = { Text("Chat ID") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (botToken.isNotBlank() && chatId.isNotBlank()) {
                        telegramViewModel.testConnection(botToken, chatId)
                    } else {
                        promptMessage = "Please enter both bot token and chat ID"
                        showMessageDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = testState !is TelegramTestState.Loading,
            ) {
                when (testState) {
                    is TelegramTestState.Loading -> {
                        Text("Testing...", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    else -> {
                        Text("TEST CONNECTION", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TelegramToggleRow(
                title = "Notifications Enabled",
                subtitle = "Send notifications via Telegram",
                checked = isConfigured,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        if (settings?.chatId != null) {
                            // Already configured
                        } else {
                            promptMessage = "Please configure bot token and chat ID first"
                            showMessageDialog = true
                        }
                    } else {
                        telegramViewModel.disableNotifications()
                    }
                },
            )

            TelegramToggleRow(
                title = "Send Research Results",
                subtitle = "Send saved research to Telegram",
                checked = settings?.sendResearch ?: true,
                onCheckedChange = { },
            )

            TelegramToggleRow(
                title = "Send Chat Results",
                subtitle = "Send AI responses to Telegram",
                checked = settings?.sendChatResults ?: false,
                onCheckedChange = { },
            )
        }
    }

    if (showMessageDialog) {
        AlertDialog(
            onDismissRequest = { showMessageDialog = false },
            title = {
                Text(
                    if (testState is TelegramTestState.Error) "Error" else "Success",
                    color = if (testState is TelegramTestState.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            },
            text = { Text(promptMessage) },
            confirmButton = {
                TextButton(onClick = { showMessageDialog = false }) { Text("OK") }
            },
        )
    }
}

@Composable
fun TelegramToggleRow(
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
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
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}
