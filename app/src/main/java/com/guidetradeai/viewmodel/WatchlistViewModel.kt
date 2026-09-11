package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SignalsRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.WatchlistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class WatchlistUiState {
    object Idle : WatchlistUiState()
    object Loading : WatchlistUiState()
    data class Success(val items: List<WatchlistItem>) : WatchlistUiState()
    data class Empty(val message: String = "Your watchlist is empty.") : WatchlistUiState()
    data class Error(val message: String) : WatchlistUiState()
}

class WatchlistViewModel(
    private val signalsRepository: SignalsRepository = AppModule.signalsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WatchlistUiState>(WatchlistUiState.Idle)
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        loadWatchlist()
    }

    fun loadWatchlist() {
        viewModelScope.launch {
            _uiState.value = WatchlistUiState.Loading
            when (val result = signalsRepository.getWatchlist()) {
                is Result.Success -> {
                    val items = result.getOrNull() ?: emptyList()
                    _uiState.value = if (items.isEmpty()) {
                        WatchlistUiState.Empty()
                    } else {
                        WatchlistUiState.Success(items)
                    }
                }
                is Result.Error -> _uiState.value = WatchlistUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun addToWatchlist(symbol: String, market: String = "crypto", timeframe: String = "1H") {
        viewModelScope.launch {
            when (val result = signalsRepository.addToWatchlist(symbol, market, timeframe)) {
                is Result.Success -> loadWatchlist()
                is Result.Error -> _uiState.value = WatchlistUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun removeFromWatchlist(itemId: String) {
        viewModelScope.launch {
            when (val result = signalsRepository.removeFromWatchlist(itemId)) {
                is Result.Success -> loadWatchlist()
                is Result.Error -> _uiState.value = WatchlistUiState.Error(result.message)
                else -> {}
            }
        }
    }
}
