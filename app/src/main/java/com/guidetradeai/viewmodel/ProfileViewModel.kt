package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.SettingsRepository
import com.guidetradeai.data.repository.TelegramRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.TelegramSettings
import com.guidetradeai.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AppModule.authRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _uiState.value = ProfileUiState.Success(user)
            } else {
                _uiState.value = ProfileUiState.Error("User not authenticated")
            }
        }
    }

    fun updateProfile(fullName: String?, avatarUrl: String?) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            when (val result = authRepository.updateProfile(fullName, avatarUrl)) {
                is Result.Success -> _uiState.value = ProfileUiState.Success(result.data)
                is Result.Error -> _uiState.value = ProfileUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
