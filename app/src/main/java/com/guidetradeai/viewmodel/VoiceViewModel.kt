package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.voice.AudioPlayer
import com.guidetradeai.voice.VoiceState
import io.github.supabase.functions.functions
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

class VoiceViewModel(
    private val settingsRepository: SettingsRepository = AppModule.settingsRepository,
    private val telegramRepository: TelegramRepository = AppModule.telegramRepository,
    private val audioPlayer: AudioPlayer = AudioPlayer(),
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
                is Result.Error -> {}
                is Result.Loading -> {}
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

    fun playTextToSpeech(text: String, onComplete: () -> Unit) {
        if (!_voiceSettings.value.voiceEnabled) {
            return
        }

        viewModelScope.launch {
            _uiState.value = VoiceUiState.Processing("Generating voice...")
            // Call text-to-speech edge function through the supabase functions
            try {
                val response = AppModule.supabaseClient.functions.invoke(
                    "text-to-speech",
                    body = """
                    {
                        "text": ${kotlinx.serialization.json.Json.encodeToString(kotlinx.serialization.json.JsonPrimitive(text))}
                    """.trimIndent(),
                )
                val data = response.data
                val json = kotlinx.serialization.json.Json.decodeFromString<kotlinx.serialization.json.JsonObject>(data)
                val audioBase64 = json.jsonObject["audio"]?.jsonPrimitive?.contentOrNull
                if (audioBase64 != null) {
                    _uiState.value = VoiceUiState.Speaking()
                    audioPlayer.playBase64Audio(audioBase64) {
                        _uiState.value = VoiceUiState.Idle
                        onComplete()
                    }
                } else {
                    _uiState.value = VoiceUiState.Error("Failed to generate voice")
                }
            } catch (e: Exception) {
                _uiState.value = VoiceUiState.Error(e.message ?: "Voice generation failed")
            }
        }
    }

    fun pausePlayback() {
        audioPlayer.pause()
    }

    fun resumePlayback() {
        audioPlayer.resume()
    }

    fun stopPlayback() {
        audioPlayer.stop()
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

data class VoiceSettings(
    val voiceEnabled: Boolean = true,
    val autoSpeak: Boolean = false,
    val theme: String = "dark",
) {
    companion object {
        val Default = VoiceSettings()
    }
}
