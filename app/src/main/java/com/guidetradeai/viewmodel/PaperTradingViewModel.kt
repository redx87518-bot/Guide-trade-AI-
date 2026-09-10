package com.guidetradeai.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.PaperTradingRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.PaperDashboardData
import com.guidetradeai.domain.model.PaperOrder
import com.guidetradeai.domain.model.PaperOrderRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PaperTradingUiState {
    object Loading : PaperTradingUiState()
    data class Success(val data: PaperDashboardData) : PaperTradingUiState()
    data class Error(val message: String) : PaperTradingUiState()
    data class OrderPlaced(val order: PaperOrder) : PaperTradingUiState()
    object OrderFailed : PaperTradingUiState()
}

class PaperTradingViewModel(
    private val paperTradingRepository: PaperTradingRepository = AppModule.paperTradingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaperTradingUiState>(PaperTradingUiState.Loading)
    val uiState: StateFlow<PaperTradingUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = PaperTradingUiState.Loading
            when (val result = paperTradingRepository.getDashboard()) {
                is Result.Success -> _uiState.value = PaperTradingUiState.Success(result.data)
                is Result.Error -> _uiState.value = PaperTradingUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun placeOrder(request: PaperOrderRequest) {
        viewModelScope.launch {
            _uiState.value = PaperTradingUiState.Loading
            when (val result = paperTradingRepository.placeOrder(request)) {
                is Result.Success -> _uiState.value = PaperTradingUiState.OrderPlaced(result.data)
                is Result.Error -> _uiState.value = PaperTradingUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
