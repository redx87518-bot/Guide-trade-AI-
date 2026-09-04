package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.AuthRepository
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.data.repository.ResearchRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import com.guidetradeai.domain.model.ChatSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Ready(
        val sessionId: String? = null,
        val sessionTitle: String = "New Chat",
        val messages: List<ChatMessage> = emptyList(),
        val isTyping: Boolean = false,
        val error: String? = null,
    ) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

class ChatViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
    private val researchRepository: ResearchRepository = AppModule.researchRepository,
    private val authRepository: AuthRepository = AppModule.authRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var isFirstMessage = false

    fun createNewSession() {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            when (val result = chatRepository.createChatSession("New Chat")) {
                is Result.Success -> {
                    isFirstMessage = true
                    _uiState.value = ChatUiState.Ready(
                        sessionId = result.data.id,
                        sessionTitle = "New Chat",
                        messages = emptyList(),
                    )
                }
                is Result.Error -> {
                    _uiState.value = ChatUiState.Error(result.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            val msgsResult = chatRepository.getChatMessages(sessionId)
            when (msgsResult) {
                is Result.Success -> {
                    _uiState.value = ChatUiState.Ready(
                        sessionId = sessionId,
                        sessionTitle = "Chat",
                        messages = msgsResult.data,
                        isTyping = false,
                    )
                }
                is Result.Error -> {
                    _uiState.value = ChatUiState.Error(msgsResult.message)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun sendMessage(message: String) {
        val sessionId = (uiState.value as? ChatUiState.Ready)?.sessionId ?: return
        if (message.isBlank()) return

        val currentState = uiState.value as? ChatUiState.Ready ?: return
        val updatedMessages = currentState.messages + ChatMessage(
            id = "local_${System.currentTimeMillis()}_user",
            sessionId = sessionId,
            userId = "",
            role = "user",
            content = message,
            createdAt = java.time.Instant.now().toString(),
        )

        _uiState.value = ChatUiState.Ready(
            sessionId = sessionId,
            sessionTitle = currentState.sessionTitle,
            messages = updatedMessages,
            isTyping = true,
            error = null,
        )

        viewModelScope.launch {
            when (val result = researchRepository.sendAiMessage(sessionId, message)) {
                is Result.Success -> {
                    val aiMsg = ChatMessage(
                        id = "local_${System.currentTimeMillis()}_ai",
                        sessionId = sessionId,
                        userId = "",
                        role = "assistant",
                        content = result.data.content,
                        createdAt = result.data.timestamp ?: java.time.Instant.now().toString(),
                    )

                    val finalMessages = (uiState.value as? ChatUiState.Ready)?.messages ?: emptyList()

                    if (isFirstMessage) {
                        val newTitle = generateTitle(message)
                        chatRepository.renameSession(sessionId, newTitle)
                        isFirstMessage = false
                        _uiState.value = ChatUiState.Ready(
                            sessionId = sessionId,
                            sessionTitle = newTitle,
                            messages = finalMessages + aiMsg,
                            isTyping = false,
                        )
                    } else {
                        _uiState.value = ChatUiState.Ready(
                            sessionId = sessionId,
                            sessionTitle = (uiState.value as? ChatUiState.Ready)?.sessionTitle ?: "Chat",
                            messages = finalMessages + aiMsg,
                            isTyping = false,
                        )
                    }
                }
                is Result.Error -> {
                    val msgs = (uiState.value as? ChatUiState.Ready)?.messages ?: emptyList()
                    _uiState.value = ChatUiState.Ready(
                        sessionId = sessionId,
                        sessionTitle = (uiState.value as? ChatUiState.Ready)?.sessionTitle ?: "Chat",
                        messages = msgs,
                        isTyping = false,
                        error = result.message,
                    )
                }
                is Result.Loading -> {}
            }
        }
    }

    fun saveResearch(title: String, response: String, asset: String?) {
        val ready = uiState.value as? ChatUiState.Ready ?: return
        val user = authRepository.getCurrentUser()
        if (user != null) {
            viewModelScope.launch {
                val query = ready.messages.lastOrNull { it.role == "user" }?.content ?: ""
                researchRepository.saveResearchResult(
                    userId = user.id,
                    sessionId = ready.sessionId ?: "",
                    title = title,
                    query = query,
                    asset = asset,
                    response = response,
                )
            }
        }
    }

    fun clearError() {
        val current = uiState.value as? ChatUiState.Ready
        if (current != null) {
            _uiState.value = current.copy(error = null)
        }
    }

    private fun generateTitle(message: String): String {
        val trimmed = message.trim()
        if (trimmed.length <= 40) return trimmed
        return trimmed.take(37) + "..."
    }
}
