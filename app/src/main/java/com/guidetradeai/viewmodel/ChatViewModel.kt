package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.SiftingIORepository
import com.guidetradeai.data.repository.StockupRepository
import com.guidetradeai.data.repository.MarketIntelligenceRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AIProvider
import com.guidetradeai.domain.model.AgentResponse
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import com.guidetradeai.domain.messageOrNull
import com.guidetradeai.audio.VoiceManager
import kotlinx.serialization.json.JsonObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import android.util.Log

class ChatViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
    private val authRepository: AuthRepository = AppModule.authRepository,
    private val voiceManager: VoiceManager = AppModule.voiceManager,
    private val stockupRepository: StockupRepository = AppModule.stockupRepository,
    private val siftingIORepository: SiftingIORepository = AppModule.siftingIORepository,
    private val marketIntelligenceRepository: MarketIntelligenceRepository = AppModule.marketIntelligenceRepository,
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

    private val _selectedProvider = MutableStateFlow(AIProvider.STOCKUP)
    val selectedProvider: StateFlow<AIProvider> = _selectedProvider.asStateFlow()

    private val _selectedFeature = MutableStateFlow("Chat")
    val selectedFeature: StateFlow<String> = _selectedFeature.asStateFlow()

    private val _selectedMarket = MutableStateFlow<String?>(null)
    val selectedMarket: StateFlow<String?> = _selectedMarket.asStateFlow()

    private val _selectedSymbol = MutableStateFlow<String?>(null)
    val selectedSymbol: StateFlow<String?> = _selectedSymbol.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("1h")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _voiceEnabled = MutableStateFlow(true)
    val voiceEnabled: StateFlow<Boolean> = _voiceEnabled.asStateFlow()

    private val _symbolSuggestions = MutableStateFlow<List<com.guidetradeai.domain.model.SymbolItem>>(emptyList())
    val symbolSuggestions: StateFlow<List<com.guidetradeai.domain.model.SymbolItem>> = _symbolSuggestions.asStateFlow()

    private val _isLoadingSymbols = MutableStateFlow(false)
    val isLoadingSymbols: StateFlow<Boolean> = _isLoadingSymbols.asStateFlow()

    private val _symbolError = MutableStateFlow<String?>(null)
    val symbolError: StateFlow<String?> = _symbolError.asStateFlow()

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

    fun setProvider(provider: AIProvider) {
        _selectedProvider.value = provider
        _selectedFeature.value = when (provider) {
            AIProvider.SIFTING_IO -> "Full Analysis"
            AIProvider.GUAVY -> "Full Analysis"
            AIProvider.COMBINED -> "Full Analysis"
            AIProvider.GUIDETRADE_AGENT -> "Full Analysis"
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
        val provider: AIProvider,
        val feature: String,
        val market: String? = null,
        val symbol: String? = null,
        val timeframe: String? = null,
    )

    private fun routeIntent(text: String): IntentRoute? {
        val lower = text.lowercase()
        val activeProvider = _selectedProvider.value
        return when {
            lower.contains("btc") || lower.contains("bitcoin") -> IntentRoute(activeProvider, "Full Analysis", "crypto", "BTCUSD")
            lower.contains("eth") || lower.contains("ethereum") -> IntentRoute(activeProvider, "Full Analysis", "crypto", "ETHUSD")
            lower.contains("eur") && lower.contains("usd") -> IntentRoute(activeProvider, "Sentiment", "forex", "EURUSD")
            lower.contains("xau") || lower.contains("gold") -> IntentRoute(activeProvider, "Full Analysis", "commodities", "XAUUSD")
            lower.contains("aapl") || lower.contains("apple") -> IntentRoute(activeProvider, "Full Analysis", "stocks", "AAPL")
            lower.contains("signal") || lower.contains("technical") -> IntentRoute(activeProvider, "Technical Signal", "crypto", "BTCUSD")
            lower.contains("sentiment") -> IntentRoute(activeProvider, "Sentiment", "crypto", "BTCUSD")
            lower.contains("news") -> IntentRoute(activeProvider, "News", "crypto", "BTCUSD")
            else -> null
        }
    }

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

            val routing = routeIntent(text)
            if (routing != null) {
                _selectedProvider.value = routing.provider
                _selectedFeature.value = routing.feature
                _selectedMarket.value = routing.market
                _selectedSymbol.value = routing.symbol
                routing.timeframe?.let { _selectedTimeframe.value = it }
            }

            val provider = _selectedProvider.value
            Log.d("ChatViewModel", "Provider selected: $provider")
            Log.d("ChatViewModel", "Market: ${_selectedMarket.value}")
            Log.d("ChatViewModel", "Symbol: ${_selectedSymbol.value}")
            Log.d("ChatViewModel", "Timeframe: ${_selectedTimeframe.value}")
            Log.d("ChatViewModel", "Analysis: ${_selectedFeature.value}")
            
            val result = when (provider) {
                AIProvider.STOCKUP -> {
                    Log.d("ChatViewModel", "Edge Function: ai-chat")
                    val res = stockupRepository.sendMessage(sessionId, text)
                    if (res is Result.Success) Result.success(res.data) else Result.error(res.messageOrNull() ?: "StockUp failed")
                }
                AIProvider.SIFTING_IO -> {
                    Log.d("ChatViewModel", "Edge Function: siftingio-market")
                    when (val miResult = siftingIORepository.query(
                        market = _selectedMarket.value?.lowercase() ?: "crypto",
                        symbol = _selectedSymbol.value ?: "BTCUSD",
                        timeframe = _selectedTimeframe.value,
                        feature = _selectedFeature.value.lowercase().replace(" ", "_"),
                        query = text,
                    )) {
                        is Result.Success -> {
                            val content = formatSiftingIOResponse(miResult.data)
                            Result.success(content)
                        }
                        is Result.Error -> Result.error(miResult.message)
                        else -> Result.error("Unknown SiftingIO error")
                    }
                }
                AIProvider.GUAVY -> {
                    Log.d("ChatViewModel", "Edge Function: market-intelligence (guavy)")
                    val request = com.guidetradeai.domain.model.MarketIntelligenceRequest(
                        provider = "guavy",
                        feature = _selectedFeature.value.lowercase().replace(" ", "_"),
                        market = _selectedMarket.value ?: "crypto",
                        symbol = _selectedSymbol.value ?: "BTC",
                        timeframe = _selectedTimeframe.value,
                        query = text,
                    )
                    when (val miResult = marketIntelligenceRepository.queryProvider(request)) {
                        is Result.Success -> {
                            val content = formatMarketResult(miResult.data.result, "GUAVY")
                            Result.success(content)
                        }
                        is Result.Error -> Result.error(miResult.message)
                        else -> Result.error("Unknown Guavy error")
                    }
                }
                AIProvider.COMBINED -> {
                    Log.d("ChatViewModel", "Edge Function: market-intelligence (combined)")
                    val request = com.guidetradeai.domain.model.MarketIntelligenceRequest(
                        provider = "combined",
                        feature = _selectedFeature.value.lowercase().replace(" ", "_"),
                        market = _selectedMarket.value ?: "crypto",
                        symbol = _selectedSymbol.value ?: "BTC",
                        timeframe = _selectedTimeframe.value,
                        query = text,
                    )
                    when (val miResult = marketIntelligenceRepository.queryProvider(request)) {
                        is Result.Success -> {
                            val content = formatCombinedResult(miResult.data.result)
                            Result.success(content)
                        }
                        is Result.Error -> Result.error(miResult.message)
                        else -> Result.error("Unknown Combined error")
                    }
                }
                AIProvider.GUIDETRADE_AGENT -> {
                    Log.d("ChatViewModel", "Edge Function: agent-orchestrator")
                    val agentRequest = com.guidetradeai.domain.model.AgentRequest(
                        goal = text,
                        sessionId = sessionId,
                        provider = "guidetrade_agent",
                        market = _selectedMarket.value,
                        symbol = _selectedSymbol.value,
                        timeframe = _selectedTimeframe.value,
                        feature = _selectedFeature.value,
                        query = text,
                    )
                    when (val agentResult = agentRepository.sendRequest(agentRequest)) {
                        is Result.Success -> {
                            val response = agentResult.data
                            val content = buildString {
                                if (!response.summary.isNullOrBlank()) {
                                    append(response.summary)
                                    append("\n\n")
                                }
                                if (!response.content.isNullOrBlank()) {
                                    append(response.content)
                                }
                                if (response.toolsUsed.isNotEmpty()) {
                                    append("\n\n**Tools Used:** ")
                                    append(response.toolsUsed.joinToString(", "))
                                }
                            }
                            Result.success(content)
                        }
                        is Result.Error -> Result.error(agentResult.message)
                        else -> Result.error("Unknown Agent error")
                    }
                }
                else -> Result.error("Provider $provider is not implemented yet")
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
                if (_voiceEnabled.value) {
                    speakResponse(result.data)
                }
            } else {
                _error.value = result.messageOrNull()
            }
            _isLoading.value = false
        }
    }

    private fun formatSiftingIOResponse(data: JsonObject): String {
        return buildString {
            append("**SIFTINGIO**\n\n")
            append("```json\n")
            append(data.toString().take(800))
            append("\n```")
        }
    }

    private fun formatMarketResult(result: JsonObject?, providerLabel: String): String {
        if (result == null) {
            return "$providerLabel returned no data."
        }
        return buildString {
            append("**$providerLabel**\n\n")
            append("```json\n")
            append(result.toString().take(1200))
            append("\n```")
        }
    }

    private fun formatCombinedResult(result: JsonObject?): String {
        if (result == null) {
            return "Combined analysis returned no data."
        }
        val siftingio = result["siftingio"]
        val guavy = result["guavy"]
        val siftingioError = (result["siftingio_error"] as? kotlinx.serialization.json.JsonPrimitive)?.content
        val guavyError = (result["guavy_error"] as? kotlinx.serialization.json.JsonPrimitive)?.content
        
        return buildString {
            append("**COMBINED ANALYSIS**\n\n")
            
            if (siftingio != null) {
                append("**SIFTINGIO**\n")
                append("```json\n")
                append(siftingio.toString().take(800))
                append("\n```\n\n")
            } else if (siftingioError != null) {
                append("**SIFTINGIO:** Error - $siftingioError\n\n")
            }
            
            if (guavy != null) {
                append("**GUAVY**\n")
                append("```json\n")
                append(guavy.toString().take(800))
                append("\n```\n\n")
            } else if (guavyError != null) {
                append("**GUAVY:** Error - $guavyError\n\n")
            }
            
            if (siftingio == null && guavy == null) {
                append("No results available from either provider.")
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

    fun loadSymbols(provider: String, market: String) {
        viewModelScope.launch {
            _isLoadingSymbols.value = true
            _symbolError.value = null
            _symbolSuggestions.value = emptyList()
            
            val result = when (provider) {
                AIProvider.SIFTING_IO -> {
                    Result.error("SiftingIO does not support symbol listing")
                }
                AIProvider.GUAVY -> {
                    marketIntelligenceRepository.listSymbols("guavy", market)
                }
                AIProvider.COMBINED -> {
                    marketIntelligenceRepository.listSymbols("combined", market)
                }
                AIProvider.GUIDETRADE_AGENT -> {
                    val agentRepo = AppModule.guideTradeAgentRepository
                    agentRepo.listSymbols("guidetrade_agent", market)
                }
                else -> {
                    Result.error("Symbol search not available for this provider")
                }
            }
            
            when (result) {
                is Result.Success -> {
                    _symbolSuggestions.value = result.data
                }
                is Result.Error -> {
                    _symbolError.value = result.message
                }
                is Result.Loading -> {}
            }
            _isLoadingSymbols.value = false
        }
    }

    private fun loadSessions(userId: String) {
        viewModelScope.launch {
            val result = chatRepository.getSessions(userId)
            if (result is Result.Success) _sessions.value = result.data
        }
    }
}
