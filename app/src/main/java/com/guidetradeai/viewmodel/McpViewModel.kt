package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.McpRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.data.repository.McpCatalogItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class McpUiState {
    object Idle : McpUiState()
    object Loading : McpUiState()
    data class Success(val catalog: List<McpCatalogItem>, val connections: List<McpCatalogItem>) : McpUiState()
    data class Error(val message: String) : McpUiState()
}

class McpViewModel(
    private val mcpRepository: McpRepository = AppModule.mcpRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<McpUiState>(McpUiState.Idle)
    val uiState: StateFlow<McpUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = McpUiState.Loading
            val catalogResult = mcpRepository.getCatalog()
            val connectionsResult = mcpRepository.getConnections()
            if (catalogResult is Result.Success && connectionsResult is Result.Success) {
                _uiState.value = McpUiState.Success(catalogResult.data, connectionsResult.data)
            } else {
                _uiState.value = McpUiState.Error("Failed to load MCP data")
            }
        }
    }
}
