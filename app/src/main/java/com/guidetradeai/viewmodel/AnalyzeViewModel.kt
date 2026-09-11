package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.SignalsRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.AnalysisRequest
import com.guidetradeai.domain.model.AnalysisResult
import com.guidetradeai.domain.model.Signal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalyzeUiState {
    object Idle : AnalyzeUiState()
    object Loading : AnalyzeUiState()
    data class Success(val result: AnalysisResult) : AnalyzeUiState()
    data class Error(val message: String) : AnalyzeUiState()
}

class AnalyzeViewModel(
    private val signalsRepository: SignalsRepository = AppModule.signalsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalyzeUiState>(AnalyzeUiState.Idle)
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    fun analyze(request: AnalysisRequest) {
        viewModelScope.launch {
            _uiState.value = AnalyzeUiState.Loading
            when (val result = signalsRepository.analyze(request)) {
                is Result.Success -> {
                    val analysis = result.getOrNull()
                    _uiState.value = if (analysis != null) {
                        AnalyzeUiState.Success(analysis)
                    } else {
                        AnalyzeUiState.Error("Analysis returned no result.")
                    }
                }
                is Result.Error -> _uiState.value = AnalyzeUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun reset() {
        _uiState.value = AnalyzeUiState.Idle
    }
}
