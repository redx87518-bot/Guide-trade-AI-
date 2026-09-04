package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(val settings: UserSettings) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository = AppModule.settingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            when (val result = settingsRepository.getUserSettings()) {
                is Result.Success -> _uiState.value = SettingsUiState.Success(result.data)
                is Result.Error -> _uiState.value = SettingsUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun updateVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            when (val result = settingsRepository.updateVoiceEnabled(enabled)) {
                is Result.Success -> {
                    val current = (uiState.value as? SettingsUiState.Success)?.settings
                    if (current != null) {
                        _uiState.value = SettingsUiState.Success(current.copy(voiceEnabled = enabled))
                    }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
    }

    fun updateAutoSpeak(enabled: Boolean) {
        viewModelScope.launch {
            when (val result = settingsRepository.updateAutoSpeak(enabled)) {
                is Result.Success -> {
                    val current = (uiState.value as? SettingsUiState.Success)?.settings
                    if (current != null) {
                        _uiState.value = SettingsUiState.Success(current.copy(autoSpeak = enabled))
                    }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            when (val result = settingsRepository.updateTheme(theme)) {
                is Result.Success -> {
                    val current = (uiState.value as? SettingsUiState.Success)?.settings
                    if (current != null) {
                        _uiState.value = SettingsUiState.Success(current.copy(theme = theme))
                    }
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
    }
}
