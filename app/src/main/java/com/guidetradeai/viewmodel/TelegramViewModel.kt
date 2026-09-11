package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.data.remote.TelegramSettingsData
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TelegramViewModel(
    private val telegramRepository: TelegramRepository = AppModule.telegramRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TelegramUiState>(TelegramUiState.Loading)
    val uiState: StateFlow<TelegramUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = TelegramUiState.Loading
            when (val result = telegramRepository.getSettings()) {
                is Result.Success -> {
                    _uiState.value = TelegramUiState.Success(result.data)
                }
                is Result.Error -> _uiState.value = TelegramUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun saveSettings(
        botTokenEncrypted: String?,
        chatId: String?,
        enabled: Boolean,
        sendResearch: Boolean,
        sendChatResults: Boolean,
    ) {
        viewModelScope.launch {
            _uiState.value = TelegramUiState.Loading
            val settings = TelegramSettingsData(
                user_id = telegramRepository.getSettings().getOrNull()?.user_id ?: "",
                bot_token_encrypted = botTokenEncrypted,
                chat_id = chatId,
                enabled = enabled,
                send_research = sendResearch,
                send_chat_results = sendChatResults,
            )
            when (val result = telegramRepository.saveSettings(settings)) {
                is Result.Success -> _uiState.value = TelegramUiState.Success(settings)
                is Result.Error -> _uiState.value = TelegramUiState.Error(result.message)
                else -> {}
            }
        }
    }
}

sealed class TelegramUiState {
    object Loading : TelegramUiState()
    data class Success(val settings: TelegramSettingsData?) : TelegramUiState()
    data class Error(val message: String) : TelegramUiState()
}
