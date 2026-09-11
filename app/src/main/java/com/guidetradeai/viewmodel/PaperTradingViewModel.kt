package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.PaperTradingRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.data.remote.PaperPositionData
import com.guidetradeai.data.remote.PaperOrderData
import com.guidetradeai.data.remote.PaperTradeData
import com.guidetradeai.data.remote.PaperOrderRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PaperTradingUiState {
    object Loading : PaperTradingUiState()
    data class Success(
        val positions: List<PaperPositionData>,
        val orders: List<PaperOrderData>,
        val trades: List<PaperTradeData>,
    ) : PaperTradingUiState()
    data class OrderPlaced(val order: PaperOrderData) : PaperTradingUiState()
    data class Error(val message: String) : PaperTradingUiState()
}

class PaperTradingViewModel(
    private val paperTradingRepository: PaperTradingRepository = AppModule.paperTradingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaperTradingUiState>(PaperTradingUiState.Loading)
    val uiState: StateFlow<PaperTradingUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = PaperTradingUiState.Loading
            val positionsResult = paperTradingRepository.getPositions()
            val ordersResult = paperTradingRepository.getOrders()
            val tradesResult = paperTradingRepository.getTrades()
            if (positionsResult is Result.Success && ordersResult is Result.Success && tradesResult is Result.Success) {
                _uiState.value = PaperTradingUiState.Success(positionsResult.data, ordersResult.data, tradesResult.data)
            } else {
                _uiState.value = PaperTradingUiState.Error("Failed to load dashboard")
            }
        }
    }

    fun placeOrder(request: PaperOrderRequest) {
        viewModelScope.launch {
            _uiState.value = PaperTradingUiState.Loading
            when (val result = paperTradingRepository.placeOrder(
                com.guidetradeai.data.remote.PaperOrderData(
                    symbol = request.symbol,
                    side = request.side,
                    quantity = request.quantity,
                    orderType = request.orderType,
                )
            )) {
                is Result.Success -> _uiState.value = PaperTradingUiState.OrderPlaced(result.data)
                is Result.Error -> _uiState.value = PaperTradingUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
