package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AgentRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarketsViewModel(
    private val agentRepository: AgentRepository = AppModule.agentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketsUiState>(MarketsUiState.Loading)
    val uiState: StateFlow<MarketsUiState> = _uiState.asStateFlow()

    fun loadMarketData(market: String = "crypto", symbol: String = "BTC") {
        viewModelScope.launch {
            _uiState.value = MarketsUiState.Loading
            val result = agentRepository.sendMessage("market", "Get market data for $symbol in $market")
            when (result) {
                is Result.Success -> _uiState.value = MarketsUiState.Success(result.data.content ?: "No data")
                is Result.Error -> _uiState.value = MarketsUiState.Error(result.message)
                else -> {}
            }
        }
    }
}

sealed class MarketsUiState {
    object Loading : MarketsUiState()
    data class Success(val data: String) : MarketsUiState()
    data class Error(val message: String) : MarketsUiState()
}
