package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.PaperTradingRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.PaperOrderRequest
import com.guidetradeai.data.remote.PaperAccountData
import com.guidetradeai.data.remote.PaperPositionData
import com.guidetradeai.data.remote.PaperOrderData
import com.guidetradeai.data.remote.PaperTradeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PaperTradingUiState {
    object Loading : PaperTradingUiState()
    data class Success(
        val account: PaperAccountData,
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

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = PaperTradingUiState.Loading
            val accountResult = paperTradingRepository.getAccount()
            val positionsResult = paperTradingRepository.getPositions()
            val ordersResult = paperTradingRepository.getOrders()
            val tradesResult = paperTradingRepository.getTrades()
            if (accountResult is Result.Success && positionsResult is Result.Success && ordersResult is Result.Success && tradesResult is Result.Success) {
                _uiState.value = PaperTradingUiState.Success(accountResult.data, positionsResult.data, ordersResult.data, tradesResult.data)
            } else {
                val errorMsg = when {
                    accountResult is Result.Error -> accountResult.message
                    positionsResult is Result.Error -> positionsResult.message
                    ordersResult is Result.Error -> ordersResult.message
                    tradesResult is Result.Error -> tradesResult.message
                    else -> "Failed to load dashboard"
                }
                _uiState.value = PaperTradingUiState.Error(errorMsg)
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
                is Result.Success -> {
                    _uiState.value = PaperTradingUiState.OrderPlaced(result.data)
                    loadDashboard()
                }
                is Result.Error -> _uiState.value = PaperTradingUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun refresh() {
        loadDashboard()
    }
}
