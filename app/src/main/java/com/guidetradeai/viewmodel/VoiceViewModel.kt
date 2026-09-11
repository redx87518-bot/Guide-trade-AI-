package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VoiceUiState {
    object Idle : VoiceUiState()
    data class Listening(val message: String = "Listening...") : VoiceUiState()
    data class Processing(val message: String = "Guide Trade is thinking...") : VoiceUiState()
    data class Speaking(val message: String = "Speaking...") : VoiceUiState()
    data class Error(val message: String) : VoiceUiState()
}

data class VoiceSettings(
    val voiceEnabled: Boolean = true,
    val autoSpeak: Boolean = false,
    val theme: String = "dark",
) {
    companion object {
        val Default = VoiceSettings()
    }
}

class VoiceViewModel(
    private val settingsRepository: SettingsRepository = AppModule.settingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<VoiceUiState>(VoiceUiState.Idle)
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private val _voiceSettings = MutableStateFlow<VoiceSettings>(VoiceSettings.Default)
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

    fun startListening(onSpeechResult: (String) -> Unit, onError: (String) -> Unit) {
        if (_voiceSettings.value.voiceEnabled) {
            _uiState.value = VoiceUiState.Listening()
        }
    }

    fun startProcessing() {
        _uiState.value = VoiceUiState.Processing()
    }

    fun pausePlayback() {}
    fun resumePlayback() {}
    fun stopPlayback() {
        _uiState.value = VoiceUiState.Idle
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
