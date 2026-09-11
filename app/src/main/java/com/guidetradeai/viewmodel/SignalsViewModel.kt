package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SignalsRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.Signal
import com.guidetradeai.domain.model.SignalFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignalsUiState {
    object Idle : SignalsUiState()
    object Loading : SignalsUiState()
    data class Success(val signals: List<Signal>) : SignalsUiState()
    data class Empty(val message: String = "No signals available yet.") : SignalsUiState()
    data class Error(val message: String) : SignalsUiState()
}

class SignalsViewModel(
    private val signalsRepository: SignalsRepository = AppModule.signalsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignalsUiState>(SignalsUiState.Idle)
    val uiState: StateFlow<SignalsUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(SignalFilter())
    val filter: StateFlow<SignalFilter> = _filter.asStateFlow()

    init {
        loadSignals()
    }

    fun loadSignals() {
        viewModelScope.launch {
            _uiState.value = SignalsUiState.Loading
            when (val result = signalsRepository.getSignals(_filter.value)) {
                is Result.Success -> {
                    val signals = result.getOrNull() ?: emptyList()
                    _uiState.value = if (signals.isEmpty()) {
                        SignalsUiState.Empty()
                    } else {
                        SignalsUiState.Success(signals)
                    }
                }
                is Result.Error -> _uiState.value = SignalsUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun updateFilter(filter: SignalFilter) {
        _filter.value = filter
        loadSignals()
    }

    fun refresh() {
        loadSignals()
    }
}
