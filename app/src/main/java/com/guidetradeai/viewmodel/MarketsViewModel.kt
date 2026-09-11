package com.guidetradeai.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.SymbolItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guidetradeai.ui.navigation.NavRoutes

sealed class MarketsUiState {
    object Loading : MarketsUiState()
    data class Success(val symbols: List<SymbolItem>, val market: String) : MarketsUiState()
    data class Error(val message: String) : MarketsUiState()
}

class MarketsViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketsUiState>(MarketsUiState.Loading)
    val uiState: StateFlow<MarketsUiState> = _uiState.asStateFlow()

    fun loadSymbols(market: String = "crypto") {
        viewModelScope.launch {
            _uiState.value = MarketsUiState.Loading
            when (val result = agentRepository.listSymbols("guidetrade_agent", market)) {
                is Result.Success -> _uiState.value = MarketsUiState.Success(result.data, market)
                is Result.Error -> _uiState.value = MarketsUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }
}
