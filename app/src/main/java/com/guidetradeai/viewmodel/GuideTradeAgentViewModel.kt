package com.guidetradeai.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.GuideTradeAgentRepository
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import com.guidetradeai.domain.model.AgentSession
import com.guidetradeai.domain.model.AgentToolCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

class GuideTradeAgentViewModel(
    private val agentRepository: GuideTradeAgentRepository = AppModule.guideTradeAgentRepository,
    private val authRepository: AuthRepository = AppModule.authRepository,
    private val appPreferences: AppPreferences = AppModule.appPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AgentUiState>(AgentUiState.Idle)
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _sessions = MutableStateFlow<List<AgentSession>>(emptyList())
    val sessions: StateFlow<List<AgentSession>> = _sessions.asStateFlow()

    private val _selectedMarket = MutableStateFlow<String?>(null)
    val selectedMarket: StateFlow<String?> = _selectedMarket.asStateFlow()

    private val _selectedSymbol = MutableStateFlow<String?>(null)
    val selectedSymbol: StateFlow<String?> = _selectedSymbol.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow<String?>(null)
    val selectedTimeframe: StateFlow<String?> = _selectedTimeframe.asStateFlow()

    private var currentRequestJob: kotlinx.coroutines.Job? = null

    fun initialize() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            loadSessions(userId)
            val lastSessionId = appPreferences.lastSessionId.first()
            if (!lastSessionId.isNullOrBlank() && sessions.value.any { it.id == lastSessionId }) {
                _currentSessionId.value = lastSessionId
            }
        }
    }

    fun sendMessage(goal: String) {
        val userId = authRepository.getCurrentUser()?.id ?: return
        val sessionId = _currentSessionId.value ?: return

        currentRequestJob?.cancel()
        currentRequestJob = viewModelScope.launch {
            _uiState.value = AgentUiState.Processing(goal, emptyList(), emptyList())

            val request = AgentRequest(
                goal = goal,
                sessionId = sessionId,
                market = _selectedMarket.value,
                symbol = _selectedSymbol.value,
                timeframe = _selectedTimeframe.value,
                query = goal,
            )

            when (val result = agentRepository.sendRequest(request)) {
                is Result.Success -> {
                    val response = result.data
                    _uiState.value = AgentUiState.Success(response)
                    if (response.sessionId != null) {
                        _currentSessionId.value = response.sessionId
                        appPreferences.saveLastSessionId(response.sessionId)
                    }
                    loadSessions(userId)
                }
                is Result.Error -> {
                    _uiState.value = AgentUiState.Error(result.message)
                }
                is Result.Loading -> {
                    _uiState.value = AgentUiState.Processing(goal, emptyList(), emptyList())
                }
            }
        }
    }

    fun startNewSession() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.id ?: return@launch
            val sessionId = UUID.randomUUID().toString()
            _currentSessionId.value = sessionId
            _uiState.value = AgentUiState.Idle
            appPreferences.saveLastSessionId(sessionId)
            loadSessions(userId)
        }
    }

    fun setMarket(market: String?) { _selectedMarket.value = market }
    fun setSymbol(symbol: String?) { _selectedSymbol.value = symbol }
    fun setTimeframe(timeframe: String?) { _selectedTimeframe.value = timeframe }
    fun clearError() { _uiState.value = AgentUiState.Idle }

    private fun loadSessions(userId: String) {
        viewModelScope.launch {
            when (val result = agentRepository.listSessions(userId)) {
                is Result.Success -> _sessions.value = result.data
                is Result.Error -> Log.w("AgentViewModel", "Failed to load sessions: ${result.message}")
                is Result.Loading -> {}
            }
        }
    }
}

sealed class AgentUiState {
    object Idle : AgentUiState()
    data class Processing(
        val goal: String,
        val toolCalls: List<AgentToolCall>,
        val tasks: List<com.guidetradeai.domain.model.AgentTask>,
    ) : AgentUiState()
    data class Success(val response: AgentResponse) : AgentUiState()
    data class Error(val message: String) : AgentUiState()
}
