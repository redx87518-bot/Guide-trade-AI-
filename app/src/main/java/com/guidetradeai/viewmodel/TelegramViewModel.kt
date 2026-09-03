package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.TelegramSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TelegramUiState {
    object Loading : TelegramUiState()
    data class Success(val settings: TelegramSettings) : TelegramUiState()
    data class Error(val message: String) : TelegramUiState()
}

sealed class TelegramTestState {
    object Idle : TelegramTestState()
    object Loading : TelegramTestState()
    data class Success(val message: String) : TelegramTestState()
    data class Error(val message: String) : TelegramTestState()
}

class TelegramViewModel(
    private val telegramRepository: TelegramRepository = AppModule.telegramRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TelegramUiState>(TelegramUiState.Loading)
    val uiState: StateFlow<TelegramUiState> = _uiState.asStateFlow()

    private val _testState = MutableStateFlow<TelegramTestState>(TelegramTestState.Idle)
    val testState: StateFlow<TelegramTestState> = _testState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = TelegramUiState.Loading
            when (val result = telegramRepository.getTelegramSettings()) {
                is Result.Success -> _uiState.value = TelegramUiState.Success(result.data)
                is Result.Error -> _uiState.value = TelegramUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun testConnection(botToken: String, chatId: String) {
        viewModelScope.launch {
            _testState.value = TelegramTestState.Loading
            when (val result = telegramRepository.testAndSaveConnection(
                botToken = botToken,
                chatId = chatId,
                sendResearch = true,
                sendChatResults = false,
            )) {
                is Result.Success -> {
                    _testState.value = TelegramTestState.Success(result.data)
                    loadSettings()
                }
                is Result.Error -> _testState.value = TelegramTestState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun saveSettings(
        botToken: String,
        chatId: String,
        enabled: Boolean,
        sendResearch: Boolean,
        sendChatResults: Boolean,
    ) {
        viewModelScope.launch {
            _uiState.value = TelegramUiState.Loading
            when (val result = telegramRepository.testAndSaveConnection(
                botToken = botToken,
                chatId = chatId,
                sendResearch = sendResearch,
                sendChatResults = sendChatResults,
            )) {
                is Result.Success -> {
                    _testState.value = TelegramTestState.Success(result.data)
                    loadSettings()
                }
                is Result.Error -> {
                    _testState.value = TelegramTestState.Error(result.message)
                    _uiState.value = TelegramUiState.Error(result.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun enableNotifications() {
        viewModelScope.launch {
            when (val result = telegramRepository.saveSettings(
                botToken = "",
                chatId = "",
                enabled = true,
                sendResearch = true,
                sendChatResults = false,
            )) {
                is Result.Success -> loadSettings()
                is Result.Error -> _uiState.value = TelegramUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun disableNotifications() {
        viewModelScope.launch {
            when (val result = telegramRepository.disableTelegram()) {
                is Result.Success -> loadSettings()
                is Result.Error -> _uiState.value = TelegramUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun maskToken(encryptedToken: String?): String {
        return telegramRepository.maskToken(encryptedToken)
    }

    fun clearTestState() {
        _testState.value = TelegramTestState.Idle
    }
}
