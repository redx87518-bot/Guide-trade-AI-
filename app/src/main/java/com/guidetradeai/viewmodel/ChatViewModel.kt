package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.audio.VoiceManager
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class ChatViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
    private val authRepository: AuthRepository = AppModule.authRepository,
    private val voiceManager: VoiceManager = AppModule.voiceManager,
    private val telegramRepository: TelegramRepository = AppModule.telegramRepository,
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _sessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val sessions: StateFlow<List<ChatSession>> = _sessions.asStateFlow()

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentSessionTitle = MutableStateFlow("New Chat")
    val currentSessionTitle: StateFlow<String> = _currentSessionTitle.asStateFlow()

    private var isFirstMessage = true
    private var voiceLoopActive = false
    private var telegramSettings: com.guidetradeai.domain.model.TelegramSettings? = null

    fun initialize() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            loadSessions(userId)
            loadTelegramSettings()
            val lastSessionId = AppModule.appPreferences.lastSessionId.first()
            val currentSessions = _sessions.value
            if (!lastSessionId.isNullOrBlank() && currentSessions.any { it.id == lastSessionId }) {
                switchSession(currentSessions.first { it.id == lastSessionId })
            } else if (_currentSessionId.value == null) {
                startNewSession()
            }
        }
    }

    fun startNewSession() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            val result = chatRepository.createSession(userId)
            if (result is Result.Success) {
                _currentSessionId.value = result.data
                _messages.value = emptyList()
                _currentSessionTitle.value = "New Chat"
                isFirstMessage = true
                AppModule.appPreferences.saveLastSessionId(result.data)
            }
        }
    }

    fun switchSession(session: ChatSession) {
        viewModelScope.launch {
            _currentSessionId.value = session.id
            _currentSessionTitle.value = session.title
            isFirstMessage = false
            val result = chatRepository.getMessages(session.id)
            if (result is Result.Success) _messages.value = result.data!!
            AppModule.appPreferences.saveLastSessionId(session.id)
        }
    }

    fun sendMessage(text: String) {
        val sessionId = _currentSessionId.value ?: return
        val userId = authRepository.getCurrentUser()?.id ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            voiceManager.stopSpeaking()

            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                userId = userId,
                role = "user",
                content = text,
                createdAt = Instant.now().toString()
            )
            _messages.value = _messages.value + userMsg

            if (isFirstMessage) {
                isFirstMessage = false
                val title = if (text.length > 40) text.take(37) + "..." else text
                _currentSessionTitle.value = title
                chatRepository.renameSession(sessionId, title)
                loadSessions(userId)
            }

            val result = chatRepository.sendMessage(sessionId, text)
            if (result is Result.Success) {
                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    userId = userId,
                    role = "assistant",
                    content = result.data!!,
                    createdAt = Instant.now().toString()
                )
                _messages.value = _messages.value + aiMsg
                speakResponse(result.data)
                sendToTelegram(text, result.data!!)
            } else {
                _error.value = (result as? Result.Error)?.message ?: "Unknown error"
            }
            _isLoading.value = false
        }
    }

    fun startVoiceLoop() {
        voiceLoopActive = true
        viewModelScope.launch {
            voiceManager.speak(
                text = "Hello, I'm Quan. How can I help you?",
                onDone = { startVoiceInput() },
                onError = { startVoiceInput() }
            )
        }
    }

    fun stopVoiceLoop() {
        voiceLoopActive = false
        stopVoiceInput()
        stopSpeaking()
    }

    fun startVoiceInput() {
        viewModelScope.launch {
            voiceManager.startListening(
                onResult = { text ->
                    _isListening.value = false
                    sendMessage(text)
                },
                onError = { error ->
                    _isListening.value = false
                    _error.value = error
                }
            )
            _isListening.value = true
        }
    }

    fun stopVoiceInput() {
        voiceManager.stopListening()
        _isListening.value = false
    }

    private fun speakResponse(text: String) {
        viewModelScope.launch {
            _isSpeaking.value = true
            voiceManager.speak(
                text = text,
                onDone = {
                    _isSpeaking.value = false
                    if (voiceLoopActive) {
                        kotlinx.coroutines.delay(300)
                        startVoiceInput()
                    }
                },
                onError = {
                    _isSpeaking.value = false
                    if (voiceLoopActive) {
                        kotlinx.coroutines.delay(300)
                        startVoiceInput()
                    }
                }
            )
        }
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        _isSpeaking.value = false
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            chatRepository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) startNewSession()
            loadSessions(userId)
        }
    }

    private suspend fun loadSessions(userId: String) {
        val result = chatRepository.getSessions(userId)
        if (result is Result.Success) _sessions.value = result.data!!
    }

    private fun loadTelegramSettings() {
        viewModelScope.launch {
            val result = telegramRepository.getTelegramSettings()
            if (result is Result.Success) {
                telegramSettings = result.data
            }
        }
    }

    private fun sendToTelegram(userMessage: String, aiResponse: String) {
        val settings = telegramSettings ?: return
        if (!settings.enabled || !settings.sendChatResults) return
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            telegramRepository.sendChatResultToTelegram(sessionId, userMessage, aiResponse)
        }
    }
}
