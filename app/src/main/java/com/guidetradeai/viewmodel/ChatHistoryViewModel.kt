package com.guidetradeai.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

sealed class ChatHistoryUiState {
    object Loading : ChatHistoryUiState()
    data class Success(val sessions: List<ChatSession>) : ChatHistoryUiState()
    data class Error(val message: String) : ChatHistoryUiState()
}

class ChatHistoryViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatHistoryUiState>(ChatHistoryUiState.Loading)
    val uiState: StateFlow<ChatHistoryUiState> = _uiState.asStateFlow()

    fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = ChatHistoryUiState.Loading
            when (val result = chatRepository.getSessions("")) {
                is Result.Success -> _uiState.value = ChatHistoryUiState.Success(result.data)
                is Result.Error -> _uiState.value = ChatHistoryUiState.Error(result.message)
                is Result.Loading -> {}
            }
        }
    }

    fun createNewSession(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val result = chatRepository.createSession("", "New Chat")
            if (result is Result.Success) {
                onCreated(result.data)
            }
            loadSessions()
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            loadSessions()
        }
    }
}
