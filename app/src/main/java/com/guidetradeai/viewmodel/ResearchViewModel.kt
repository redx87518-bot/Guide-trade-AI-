package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ResearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ResearchHistoryUiState {
    object Loading : ResearchHistoryUiState()
    data class Success(val results: List<ResearchResult>) : ResearchHistoryUiState()
    data class Error(val message: String) : ResearchHistoryUiState()
}

class ResearchViewModel(
    private val researchRepository: ResearchRepository = AppModule.researchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResearchHistoryUiState>(ResearchHistoryUiState.Loading)
    val uiState: StateFlow<ResearchHistoryUiState> = _uiState.asStateFlow()

    fun loadResearchHistory() {
        viewModelScope.launch {
            _uiState.value = ResearchHistoryUiState.Loading
            when (val result = researchRepository.getResearchHistory()) {
                is Result.Success -> _uiState.value = ResearchHistoryUiState.Success(result.data)
                is Result.Error -> _uiState.value = ResearchHistoryUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun deleteResearch(id: String) {
        viewModelScope.launch {
            when (val result = researchRepository.deleteResearchResult(id)) {
                is Result.Success -> {
                    val current = (uiState.value as? ResearchHistoryUiState.Success)?.results
                        ?.filterNot { it.id == id } ?: emptyList()
                    _uiState.value = ResearchHistoryUiState.Success(current)
                }
                is Result.Error -> {}
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadHistory()
    }
}
