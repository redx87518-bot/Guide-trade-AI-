package com.guidetradeai.voice

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

sealed class VoiceUiState {
    object Idle : VoiceUiState()
    object Listening : VoiceUiState()
    object Processing : VoiceUiState()
    object Speaking : VoiceUiState()
    data class Error(val message: String) : VoiceUiState()
}
