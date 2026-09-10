package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import com.guidetradeai.audio.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import android.util.Log

class ChatViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
    private val authRepository: AuthRepository = AppModule.authRepository,
    private val voiceManager: VoiceManager = AppModule.voiceManager,
    private val agentRepository: GuideTradeAgentRepository = AppModule.guideTradeAgentRepository,
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

    private val _selectedMarket = MutableStateFlow<String?>(null)
    val selectedMarket: StateFlow<String?> = _selectedMarket.asStateFlow()

    private val _selectedSymbol = MutableStateFlow<String?>(null)
    val selectedSymbol: StateFlow<String?> = _selectedSymbol.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow<String?>(null)
    val selectedTimeframe: StateFlow<String?> = _selectedTimeframe.asStateFlow()

    private val _voiceEnabled = MutableStateFlow(true)
    val voiceEnabled: StateFlow<Boolean> = _voiceEnabled.asStateFlow()

    private var isFirstMessage = true
    private var currentRequestJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            _voiceEnabled.value = AppModule.appPreferences.voiceEnabled.first()
        }
    }

    fun initialize() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            loadSessions(userId)
            val lastSessionId = AppModule.appPreferences.lastSessionId.first()
            if (!lastSessionId.isNullOrBlank() && sessions.value.any { it.id == lastSessionId }) {
                switchSession(sessions.value.first { it.id == lastSessionId })
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
            if (result is Result.Success) _messages.value = result.data
            AppModule.appPreferences.saveLastSessionId(session.id)
        }
    }

    fun setMarket(market: String?) { _selectedMarket.value = market }
    fun setSymbol(symbol: String?) { _selectedSymbol.value = symbol }
    fun setTimeframe(timeframe: String?) { _selectedTimeframe.value = timeframe }

    fun sendMessage(text: String) {
        val sessionId = _currentSessionId.value ?: return
        val userId = authRepository.getCurrentUser()?.id ?: return

        currentRequestJob?.cancel()
        currentRequestJob = viewModelScope.launch {
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

            val agentRequest = AgentRequest(
                goal = text,
                sessionId = sessionId,
                market = _selectedMarket.value,
                symbol = _selectedSymbol.value,
                timeframe = _selectedTimeframe.value,
                query = text,
            )

            Log.d("ChatViewModel", "GuideTrade Agent request: $text")

            when (val result = agentRepository.sendRequest(agentRequest)) {
                is Result.Success -> {
                    val response = result.data
                    val content = buildString {
                        if (!response.summary.isNullOrBlank()) {
                            append(response.summary)
                            append("\n\n")
                        }
                        if (!response.content.isNullOrBlank()) {
                            append(response.content)
                        }
                    }
                    val aiMsg = ChatMessage(
                        id = UUID.randomUUID().toString(),
                        sessionId = sessionId,
                        userId = userId,
                        role = "assistant",
                        content = content,
                        createdAt = Instant.now().toString(),
                        marketData = response.marketData,
                    )
                    _messages.value = _messages.value + aiMsg
                    if (_voiceEnabled.value) {
                        speakResponse(content)
                    }
                }
                is Result.Error -> {
                    _error.value = result.message
                }
                is Result.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun startVoiceInput() {
        _isListening.value = true
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
                onDone = { _isSpeaking.value = false },
                onError = { _isSpeaking.value = false }
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

    private fun loadSessions(userId: String) {
        viewModelScope.launch {
            val result = chatRepository.getSessions(userId)
            if (result is Result.Success) _sessions.value = result.data
        }
    }
}