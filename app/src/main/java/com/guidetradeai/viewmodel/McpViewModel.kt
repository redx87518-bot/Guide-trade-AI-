package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.McpRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.data.remote.McpConnectionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class McpViewModel(
    private val mcpRepository: McpRepository = AppModule.mcpRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<McpUiState>(McpUiState.Loading)
    val uiState: StateFlow<McpUiState> = _uiState.asStateFlow()

    init {
        loadConnections()
    }

    fun loadConnections() {
        viewModelScope.launch {
            _uiState.value = McpUiState.Loading
            when (val result = mcpRepository.getConnections()) {
                is Result.Success -> _uiState.value = McpUiState.Success(result.data)
                is Result.Error -> _uiState.value = McpUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun saveConnection(name: String, serverUrl: String, apiKey: String?, enabled: Boolean) {
        viewModelScope.launch {
            _uiState.value = McpUiState.Loading
            val connection = McpConnectionData(
                name = name,
                server_url = serverUrl,
                api_key = apiKey,
                enabled = enabled,
            )
            when (val result = mcpRepository.saveConnection(connection)) {
                is Result.Success -> loadConnections()
                is Result.Error -> _uiState.value = McpUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun deleteConnection(connectionId: String) {
        viewModelScope.launch {
            _uiState.value = McpUiState.Loading
            when (val result = mcpRepository.deleteConnection(connectionId)) {
                is Result.Success -> loadConnections()
                is Result.Error -> _uiState.value = McpUiState.Error(result.message)
                else -> {}
            }
        }
    }
}

sealed class McpUiState {
    object Loading : McpUiState()
    data class Success(val connections: List<McpConnectionData>) : McpUiState()
    data class Error(val message: String) : McpUiState()
}
