package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VoiceViewModel(
    private val settingsRepository: SettingsRepository = AppModule.settingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VoiceUiState>(VoiceUiState.Idle)
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private val _voiceSettings = MutableStateFlow(VoiceSettings())
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    init {
        loadVoiceSettings()
    }

    private fun loadVoiceSettings() {
        viewModelScope.launch {
            when (val result = settingsRepository.getUserSettings()) {
                is Result.Success -> {
                    _voiceSettings.value = VoiceSettings(
                        voiceEnabled = result.data.voiceEnabled,
                        autoSpeak = result.data.autoSpeak,
                        theme = result.data.theme,
                    )
                }
                else -> {}
            }
        }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateVoiceEnabled(enabled)
            _voiceSettings.value = _voiceSettings.value.copy(voiceEnabled = enabled)
        }
    }

    fun setAutoSpeak(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAutoSpeak(enabled)
            _voiceSettings.value = _voiceSettings.value.copy(autoSpeak = enabled)
        }
    }

    fun clearError() {
        _uiState.value = VoiceUiState.Idle
    }
}

data class VoiceSettings(
    val voiceEnabled: Boolean = true,
    val autoSpeak: Boolean = false,
    val theme: String = "dark",
)

sealed class VoiceUiState {
    object Idle : VoiceUiState()
    data class Listening(val message: String = "Listening...") : VoiceUiState()
    data class Processing(val message: String = "Processing...") : VoiceUiState()
    data class Speaking(val message: String = "Speaking...") : VoiceUiState()
    data class Error(val message: String) : VoiceUiState()
}
