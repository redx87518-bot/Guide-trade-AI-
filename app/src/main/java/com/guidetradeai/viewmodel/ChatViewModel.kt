package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.MarketIntelligenceRepository
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import com.guidetradeai.domain.model.MarketIntelligenceRequest
import com.guidetradeai.domain.messageOrNull
import com.guidetradeai.audio.VoiceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class ChatViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
    private val authRepository: AuthRepository = AppModule.authRepository,
    private val voiceManager: VoiceManager = AppModule.voiceManager,
    private val marketIntelligenceRepository: MarketIntelligenceRepository = MarketIntelligenceRepository(AppModule.supabaseClient),
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

    private val _selectedProvider = MutableStateFlow("StockUp")
    val selectedProvider: StateFlow<String> = _selectedProvider.asStateFlow()

    private val _selectedFeature = MutableStateFlow("Chat")
    val selectedFeature: StateFlow<String> = _selectedFeature.asStateFlow()

    private val _selectedMarket = MutableStateFlow<String?>(null)
    val selectedMarket: StateFlow<String?> = _selectedMarket.asStateFlow()

    private val _selectedSymbol = MutableStateFlow<String?>(null)
    val selectedSymbol: StateFlow<String?> = _selectedSymbol.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("1h")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private var isFirstMessage = true

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

    fun setProvider(provider: String) {
        _selectedProvider.value = provider
        _selectedFeature.value = when (provider) {
            "SiftingIO" -> "Full Analysis"
            "Guavy" -> "Full Analysis"
            "Combined" -> "Full Analysis"
            else -> "Chat"
        }
        _selectedMarket.value = null
        _selectedSymbol.value = null
    }

    fun setFeature(feature: String) { _selectedFeature.value = feature }
    fun setMarket(market: String?) { _selectedMarket.value = market }
    fun setSymbol(symbol: String?) { _selectedSymbol.value = symbol }
    fun setTimeframe(timeframe: String) { _selectedTimeframe.value = timeframe }

    data class IntentRoute(
        val provider: String,
        val feature: String,
        val market: String? = null,
        val symbol: String? = null,
        val timeframe: String? = null,
    )

    private fun routeIntent(text: String): IntentRoute? {
        val lower = text.lowercase()
        val activeProvider = _selectedProvider.value
        return when {
            lower.contains("btc") || lower.contains("bitcoin") -> IntentRoute(activeProvider, "Full Analysis", "crypto", "BTC")
            lower.contains("eth") || lower.contains("ethereum") -> IntentRoute(activeProvider, "Full Analysis", "crypto", "ETH")
            lower.contains("eur") && lower.contains("usd") -> IntentRoute(activeProvider, "Sentiment", "forex", "EURUSD")
            lower.contains("xau") || lower.contains("gold") -> IntentRoute(activeProvider, "Full Analysis", "commodities", "XAUUSD")
            lower.contains("aapl") || lower.contains("apple") -> IntentRoute(activeProvider, "Full Analysis", "stocks", "AAPL")
            lower.contains("signal") || lower.contains("technical") -> IntentRoute(activeProvider, "Technical Signal", "crypto", "BTCUSD")
            lower.contains("sentiment") -> IntentRoute(activeProvider, "Sentiment", "crypto", "BTC")
            lower.contains("news") -> IntentRoute(activeProvider, "News", "crypto", "BTC")
            else -> null
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

            val routing = routeIntent(text)
            if (routing != null) {
                _selectedProvider.value = routing.provider
                _selectedFeature.value = routing.feature
                _selectedMarket.value = routing.market
                _selectedSymbol.value = routing.symbol
                routing.timeframe?.let { _selectedTimeframe.value = it }
            }

            val provider = _selectedProvider.value
            val result = when (provider) {
                "StockUp" -> chatRepository.sendMessage(sessionId, text)
                else -> {
                    val request = com.guidetradeai.domain.model.MarketIntelligenceRequest(
                        provider = provider.lowercase(),
                        feature = _selectedFeature.value.lowercase().replace(" ", "_"),
                        market = _selectedMarket.value?.lowercase(),
                        symbol = _selectedSymbol.value,
                        timeframe = _selectedTimeframe.value,
                        query = text,
                    )
                    when (val miResult = marketIntelligenceRepository.queryProvider(request)) {
                        is Result.Success -> {
                            val content = formatMarketIntelligenceResponse(miResult.data)
                            Result.success(content)
                        }
                        is Result.Error -> Result.error(miResult.message)
                        else -> Result.error("Unknown error")
                    }
                }
            }

            if (result is Result.Success) {
                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    userId = userId,
                    role = "assistant",
                    content = result.data,
                    createdAt = Instant.now().toString()
                )
                _messages.value = _messages.value + aiMsg
                speakResponse(result.data)
            } else {
                _error.value = result.messageOrNull()
            }
            _isLoading.value = false
        }
    }

    private fun formatMarketIntelligenceResponse(response: com.guidetradeai.domain.model.MarketIntelligenceResponse): String {
        val provider = response.provider.uppercase()
        val symbol = response.symbol ?: response.market ?: "Market"
        val result = response.result
        
        return buildString {
            append("**$provider — $symbol**\n\n")
            if (result != null) {
                append("Feature: ${response.feature}\n")
                append("Time: ${response.timeframe ?: "N/A"}\n\n")
                append("```json\n")
                append(result.toString().take(500))
                append("\n```")
            } else {
                append("No data available.")
            }
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
            val autoSpeak = AppModule.appPreferences.autoSpeak.first()
            val voiceEnabled = AppModule.appPreferences.voiceEnabled.first()
            if (!autoSpeak || !voiceEnabled) {
                _isSpeaking.value = false
                return@launch
            }
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
