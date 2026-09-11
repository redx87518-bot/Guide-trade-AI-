package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.data.remote.ChatMessageData
import com.guidetradeai.data.remote.ChatSessionData
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun getSessions(): Result<List<ChatSessionData>> {
        return supabase.getChatSessions()
    }

    suspend fun createSession(title: String = "New Chat"): Result<ChatSessionData> {
        return supabase.createChatSession(title)
    }

    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return supabase.deleteChatSession(sessionId)
    }

    suspend fun getMessages(sessionId: String): Result<List<ChatMessageData>> {
        return supabase.getChatMessages(sessionId)
    }

    suspend fun saveMessage(message: ChatMessageData): Result<ChatMessageData> {
        return supabase.saveChatMessage(message)
    }
}
