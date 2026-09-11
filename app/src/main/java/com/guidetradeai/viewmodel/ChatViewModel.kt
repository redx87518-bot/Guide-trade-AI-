package com.guidetradeai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guidetradeai.data.repository.ChatRepository
import com.guidetradeai.di.AppModule
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository = AppModule.chatRepository,
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun loadMessages(sessionId: String) {
        viewModelScope.launch {
            when (val result = chatRepository.getMessages(sessionId)) {
                is Result.Success -> {
                    _messages.value = result.data.map {
                        ChatMessage(
                            id = it.id,
                            sessionId = it.sessionId,
                            role = it.role,
                            content = it.content,
                            createdAt = it.createdAt,
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun sendMessage(sessionId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _isProcessing.value = true
            val userMessage = ChatMessage(
                sessionId = sessionId,
                role = "user",
                content = content,
            )
            _messages.value = _messages.value + userMessage

            chatRepository.saveMessage(
                com.guidetradeai.data.remote.ChatMessageData(
                    sessionId = sessionId,
                    userId = "",
                    role = "user",
                    content = content,
                )
            )

            when (val result = chatRepository.getMessages(sessionId)) {
                is Result.Success -> {
                    val assistantMessage = ChatMessage(
                        sessionId = sessionId,
                        role = "assistant",
                        content = "Echo: $content",
                    )
                    _messages.value = _messages.value + assistantMessage
                }
                else -> {}
            }
            _isProcessing.value = false
        }
    }
}
