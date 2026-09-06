package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.MarketIntelligenceRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.model.MarketIntelligenceRequest
import com.guidetradeai.domain.model.MarketIntelligenceResponse
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MarketIntelligenceUiState {
    object Idle : MarketIntelligenceUiState()
    object Loading : MarketIntelligenceUiState()
    data class Success(val response: MarketIntelligenceResponse) : MarketIntelligenceUiState()
    data class Error(val message: String) : MarketIntelligenceUiState()
}

class MarketIntelligenceViewModel(
    private val marketIntelligenceRepository: MarketIntelligenceRepository = MarketIntelligenceRepository(AppModule.supabaseClient),
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketIntelligenceUiState>(MarketIntelligenceUiState.Idle)
    val uiState: StateFlow<MarketIntelligenceUiState> = _uiState.asStateFlow()

    fun query(request: MarketIntelligenceRequest) {
        viewModelScope.launch {
            _uiState.value = MarketIntelligenceUiState.Loading
            when (val result = marketIntelligenceRepository.queryProvider(request)) {
                is Result.Success -> _uiState.value = MarketIntelligenceUiState.Success(result.data)
                is Result.Error -> _uiState.value = MarketIntelligenceUiState.Error(result.message)
                else -> _uiState.value = MarketIntelligenceUiState.Error("Unknown error")
            }
        }
    }

    fun reset() {
        _uiState.value = MarketIntelligenceUiState.Idle
    }
}
