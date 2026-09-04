package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ResearchResult
import com.guidetradeai.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val user: User,
        val recentResearch: List<ResearchResult>,
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val authRepository: AuthRepository = AppModule.authRepository,
    private val researchRepository: ResearchRepository = AppModule.researchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val user = authRepository.getCurrentUser()
            if (user == null) {
                _uiState.value = HomeUiState.Error("User not authenticated")
                return@launch
            }
            when (val result = researchRepository.getResearchHistory()) {
                is Result.Success -> {
                    val recent = result.data.take(5)
                    _uiState.value = HomeUiState.Success(user, recent)
                }
                is Result.Error -> {
                    _uiState.value = HomeUiState.Success(user, emptyList())
                }
                is Result.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadHome()
    }
}
