package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AgentRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String = "user",
    val content: String = "",
    val summary: String? = null,
    val timestamp: String? = null,
)

sealed class AgentUiState {
    object Idle : AgentUiState()
    object Loading : AgentUiState()
    data class Success(val response: AgentResponse) : AgentUiState()
    data class Error(val message: String) : AgentUiState()
}

class AgentViewModel(
    private val agentRepository: AgentRepository = AppModule.agentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AgentUiState>(AgentUiState.Idle)
    val uiState: StateFlow<AgentUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(query: String) {
        val userMessage = ChatMessage(role = "user", content = query)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _uiState.value = AgentUiState.Loading
            val sessionId = UUID.randomUUID().toString()
            when (val result = agentRepository.sendMessage(sessionId, query)) {
                is Result.Success -> {
                    val response = result.getOrNull()
                    _uiState.value = if (response != null) {
                        AgentUiState.Success(response)
                    } else {
                        AgentUiState.Error("No response from agent.")
                    }
                    val agentMessage = ChatMessage(
                        role = "agent",
                        content = response?.content ?: "Analysis complete",
                        summary = response?.summary,
                        timestamp = response?.timestamp,
                    )
                    _messages.value = _messages.value + agentMessage
                }
                is Result.Error -> {
                    _uiState.value = AgentUiState.Error(result.message)
                    val errorMessage = ChatMessage(
                        role = "agent",
                        content = "Error: ${result.message}",
                    )
                    _messages.value = _messages.value + errorMessage
                }
                else -> {}
            }
        }
    }

    fun reset() {
        _uiState.value = AgentUiState.Idle
    }
}
