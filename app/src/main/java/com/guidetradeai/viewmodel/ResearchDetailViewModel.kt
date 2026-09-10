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

sealed class ResearchDetailUiState {
    object Loading : ResearchDetailUiState()
    data class Success(val result: ResearchResult) : ResearchDetailUiState()
    data class Error(val message: String) : ResearchDetailUiState()
}

sealed class TelegramActionState {
    object Idle : TelegramActionState()
    object Loading : TelegramActionState()
    object Success : TelegramActionState()
    data class Error(val message: String) : TelegramActionState()
}

class ResearchDetailViewModel(
    private val researchRepository: ResearchRepository = AppModule.researchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResearchDetailUiState>(ResearchDetailUiState.Loading)
    val uiState: StateFlow<ResearchDetailUiState> = _uiState.asStateFlow()

    private val _telegramState = MutableStateFlow<TelegramActionState>(TelegramActionState.Idle)
    val telegramState: StateFlow<TelegramActionState> = _telegramState.asStateFlow()

    fun loadResearch(id: String) {
        viewModelScope.launch {
            _uiState.value = ResearchDetailUiState.Loading
            when (val result = researchRepository.getResearchResult(id)) {
                is Result.Success -> _uiState.value = ResearchDetailUiState.Success(result.data)
                is Result.Error -> _uiState.value = ResearchDetailUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun sendToTelegram(
        title: String,
        response: String,
        asset: String?,
        researchId: String?,
    ) {
        viewModelScope.launch {
            _telegramState.value = TelegramActionState.Loading
            when (val result = researchRepository.sendToTelegram(title, "", response, asset, researchId)) {
                is Result.Success -> _telegramState.value = TelegramActionState.Success
                is Result.Error -> _telegramState.value = TelegramActionState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun deleteResearch(id: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            when (val result = researchRepository.deleteResearchResult(id)) {
                is Result.Success -> onComplete()
                is Result.Error -> onComplete()
                is Result.Loading -> {}
            }
        }
    }

    fun clearTelegramState() {
        _telegramState.value = TelegramActionState.Idle
    }
}
