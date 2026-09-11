package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AgentRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AgentRequest
import com.guidetradeai.domain.model.AgentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun sendMessage(query: String, context: AgentRequest? = null) {
        viewModelScope.launch {
            _uiState.value = AgentUiState.Loading
            val request = context ?: AgentRequest(
                goal = query,
                query = query,
            )
            when (val result = agentRepository.sendMessage(request)) {
                is Result.Success -> {
                    val response = result.getOrNull()
                    _uiState.value = if (response != null) {
                        AgentUiState.Success(response)
                    } else {
                        AgentUiState.Error("No response from agent.")
                    }
                }
                is Result.Error -> _uiState.value = AgentUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun reset() {
        _uiState.value = AgentUiState.Idle
    }
}
