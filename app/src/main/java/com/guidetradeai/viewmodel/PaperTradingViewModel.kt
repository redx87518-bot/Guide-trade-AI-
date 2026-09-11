package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.PaperTradingRepository
import com.guidetradeai.data.repository.McpRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.data.remote.PaperOrderData
import com.guidetradeai.data.remote.PaperPositionData
import com.guidetradeai.data.remote.PaperTradeData
import com.guidetradeai.data.remote.McpConnectionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaperTradingViewModel(
    private val paperTradingRepository: PaperTradingRepository = AppModule.paperTradingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaperTradingUiState>(PaperTradingUiState.Loading)
    val uiState: StateFlow<PaperTradingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = PaperTradingUiState.Loading
            val positions = paperTradingRepository.getPositions()
            val orders = paperTradingRepository.getOrders()
            val trades = paperTradingRepository.getTrades()
            
            when {
                positions is Result.Success && orders is Result.Success && trades is Result.Success -> {
                    _uiState.value = PaperTradingUiState.Success(
                        positions = positions.data,
                        orders = orders.data,
                        trades = trades.data,
                    )
                }
                positions is Result.Error -> _uiState.value = PaperTradingUiState.Error(positions.message)
                orders is Result.Error -> _uiState.value = PaperTradingUiState.Error(orders.message)
                trades is Result.Error -> _uiState.value = PaperTradingUiState.Error(trades.message)
                else -> _uiState.value = PaperTradingUiState.Error("Failed to load data")
            }
        }
    }

    fun placeOrder(symbol: String, side: String, quantity: Double, price: Double) {
        viewModelScope.launch {
            val order = PaperOrderData(
                symbol = symbol,
                side = side,
                quantity = quantity,
                price = price,
                status = "pending",
            )
            when (val result = paperTradingRepository.placeOrder(order)) {
                is Result.Success -> loadData()
                is Result.Error -> _uiState.value = PaperTradingUiState.Error(result.message)
                else -> {}
            }
        }
    }
}

sealed class PaperTradingUiState {
    object Loading : PaperTradingUiState()
    data class Success(
        val positions: List<PaperPositionData>,
        val orders: List<PaperOrderData>,
        val trades: List<PaperTradeData>,
    ) : PaperTradingUiState()
    data class Error(val message: String) : PaperTradingUiState()
}
