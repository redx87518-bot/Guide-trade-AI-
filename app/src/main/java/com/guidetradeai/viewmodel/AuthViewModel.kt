package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Authenticated(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AppModule.authRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(email: String, password: String, fullName: String? = null) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.signUp(email, password, fullName)) {
                is Result.Success -> _uiState.value = AuthUiState.Authenticated(authRepository.currentUserId())
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.signIn(email, password)) {
                is Result.Success -> _uiState.value = AuthUiState.Authenticated(authRepository.currentUserId())
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                else -> {}
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.resetPassword(email)) {
                is Result.Success -> _uiState.value = AuthUiState.Idle
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                else -> {}
            }
        }
    }

    init {
        if (authRepository.isLoggedIn()) {
            _uiState.value = AuthUiState.Authenticated(authRepository.currentUserId())
        }
    }
}
