package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Loading : AuthUiState()
    object Unauthenticated : AuthUiState()
    data class Authenticated(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    object ResetPasswordSent : AuthUiState()
    object VerificationSent : AuthUiState()
    data class Unverified(val email: String, val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository = AppModule.authRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            if (authRepository.isUserAuthenticated()) {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _uiState.value = AuthUiState.Authenticated(user)
                } else {
                    _uiState.value = AuthUiState.Unauthenticated
                }
            } else {
                _uiState.value = AuthUiState.Unauthenticated
            }
        }
    }

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.signUp(email, password, fullName)) {
                is Result.Success -> {
                    _uiState.value = AuthUiState.VerificationSent
                }
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.signIn(email, password)) {
                is Result.Success -> _uiState.value = AuthUiState.Authenticated(result.data)
                is Result.Error -> {
                    val lower = result.message.lowercase()
                    if (lower.contains("verify") || lower.contains("not confirmed") || lower.contains("email")) {
                        _uiState.value = AuthUiState.Unverified(email, result.message)
                    } else {
                        _uiState.value = AuthUiState.Error(result.message)
                    }
                }
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.signOut()) {
                is Result.Success -> _uiState.value = AuthUiState.Unauthenticated
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = authRepository.resetPassword(email)) {
                is Result.Success -> _uiState.value = AuthUiState.ResetPasswordSent
                is Result.Error -> _uiState.value = AuthUiState.Error(result.message)
                is Result.Loading -> _uiState.value = AuthUiState.Loading
            }
        }
    }

    fun resendVerificationEmail(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authRepository.resetPassword(email)
                _uiState.value = AuthUiState.VerificationSent
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to resend verification email")
            }
        }
    }
}
