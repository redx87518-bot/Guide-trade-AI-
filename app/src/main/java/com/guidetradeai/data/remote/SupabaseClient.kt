package com.guidetradeai.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.datetime.Clock

object SupabaseClient {
    const val URL = "https://dnfutvafibliysnsetwm.supabase.co"
    const val ANON_KEY = "sb_publishable_Y-kuMPpPKDT9NKKi9fCKcw_YaVdkIpL"

    val client: SupabaseClient by lazy {
        io.github.jan.supabase.createSupabaseClient(
            supabaseUrl = URL,
            supabaseKey = ANON_KEY,
        ) {
            install(io.github.jan.supabase.auth.Auth)
            install(io.github.jan.supabase.postgrest.Postgrest)
            install(io.github.jan.supabase.functions.Functions)
            install(io.github.jan.supabase.realtime.Realtime)
        }
    }
}

object SupabaseClientWrapper {
    private val client by lazy { SupabaseClient.client }

    val auth: Auth by lazy { client.auth }

    suspend fun signUp(email: String, password: String, fullName: String? = null): Result<io.github.jan.supabase.auth.user.UserSession> {
        return try {
            val session = client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = mapOf("full_name" to fullName)
            }
            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign up failed")
        }
    }

    suspend fun signIn(email: String, password: String): Result<io.github.jan.supabase.auth.user.UserSession> {
        return try {
            val session = client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign in failed")
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    fun currentUser(): io.github.jan.supabase.auth.user.UserSession? {
        return client.auth.currentSessionOrNull()
    }

    fun currentUserId(): String {
        return client.auth.currentSessionOrNull()?.user?.id ?: ""
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            client.auth.resetPasswordForEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Password reset failed")
        }
    }

    suspend fun updateProfile(fullName: String? = null, avatarUrl: String? = null): Result<Unit> {
        return try {
            val userId = currentUserId()
            client.postgrest["profiles"].update(
                mapOf(
                    "full_name" to fullName,
                    "avatar_url" to avatarUrl,
                    "updated_at" to Clock.System.now().toString(),
                )
            ) {
                filter { eq("id", userId) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Profile update failed")
        }
    }

    suspend fun getUserSettings(): Result<UserSettingsData> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["user_settings"].select {
                filter { eq("user_id", userId) }
            }
            val settings = response.decodeList<UserSettingsData>()
            if (settings.isNotEmpty()) {
                Result.Success(settings.first())
            } else {
                Result.Error("Settings not found")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load settings")
        }
    }

    suspend fun updateUserSettings(settings: UserSettingsData): Result<Unit> {
        return try {
            val userId = currentUserId()
            client.postgrest["user_settings"].update(
                mapOf(
                    "voice_enabled" to settings.voice_enabled,
                    "auto_speak" to settings.auto_speak,
                    "theme" to settings.theme,
                    "updated_at" to Clock.System.now().toString(),
                )
            ) {
                filter { eq("user_id", userId) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update settings")
        }
    }

    suspend fun getChatSessions(): Result<List<ChatSessionData>> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["chat_sessions"].select {
                filter { eq("user_id", userId) }
                order("updated_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load chat sessions")
        }
    }

    suspend fun createChatSession(title: String = "New Chat"): Result<ChatSessionData> {
        return try {
            val userId = currentUserId()
            val session = client.postgrest["chat_sessions"].insert(
                mapOf(
                    "user_id" to userId,
                    "title" to title,
                )
            ) {
                select()
            }.decodeSingle<ChatSessionData>()
            Result.Success(session)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create chat session")
        }
    }

    suspend fun deleteChatSession(sessionId: String): Result<Unit> {
        return try {
            val userId = currentUserId()
            client.postgrest["chat_sessions"].delete {
                filter {
                    and {
                        eq("id", sessionId)
                        eq("user_id", userId)
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete session")
        }
    }

    suspend fun getChatMessages(sessionId: String): Result<List<ChatMessageData>> {
        return try {
            val response = client.postgrest["chat_messages"].select {
                filter { eq("session_id", sessionId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load messages")
        }
    }

    suspend fun saveChatMessage(message: ChatMessageData): Result<ChatMessageData> {
        return try {
            val saved = client.postgrest["chat_messages"].insert(message) {
                select()
            }.decodeSingle<ChatMessageData>()
            Result.Success(saved)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save message")
        }
    }

    suspend fun getResearchResults(): Result<List<ResearchResultData>> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["research_results"].select {
                filter { eq("user_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load research results")
        }
    }

    suspend fun saveResearchResult(result: ResearchResultData): Result<ResearchResultData> {
        return try {
            val saved = client.postgrest["research_results"].insert(result) {
                select()
            }.decodeSingle<ResearchResultData>()
            Result.Success(saved)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save research result")
        }
    }

    suspend fun getTelegramSettings(): Result<TelegramSettingsData?> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["telegram_settings"].select {
                filter { eq("user_id", userId) }
            }
            val settings = response.decodeList<TelegramSettingsData>()
            Result.Success(settings.firstOrNull())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load telegram settings")
        }
    }

    suspend fun saveTelegramSettings(settings: TelegramSettingsData): Result<Unit> {
        return try {
            val userId = currentUserId()
            client.postgrest["telegram_settings"].upsert(
                mapOf(
                    "user_id" to userId,
                    "bot_token_encrypted" to settings.bot_token_encrypted,
                    "chat_id" to settings.chat_id,
                    "enabled" to settings.enabled,
                    "send_research" to settings.send_research,
                    "send_chat_results" to settings.send_chat_results,
                )
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save telegram settings")
        }
    }

    suspend fun getPaperPositions(): Result<List<PaperPositionData>> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["paper_positions"].select {
                filter { eq("user_id", userId) }
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load positions")
        }
    }

    suspend fun getPaperOrders(): Result<List<PaperOrderData>> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["paper_orders"].select {
                filter { eq("user_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load orders")
        }
    }

    suspend fun placePaperOrder(order: PaperOrderData): Result<PaperOrderData> {
        return try {
            val userId = currentUserId()
            val created = client.postgrest["paper_orders"].insert(
                mapOf(
                    "user_id" to userId,
                    "symbol" to order.symbol,
                    "side" to order.side,
                    "quantity" to order.quantity,
                    "price" to order.price,
                    "status" to order.status,
                )
            ) {
                select()
            }.decodeSingle<PaperOrderData>()
            Result.Success(created)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to place order")
        }
    }

    suspend fun getPaperTrades(): Result<List<PaperTradeData>> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["paper_trades"].select {
                filter { eq("user_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load trades")
        }
    }

    suspend fun getMcpConnections(): Result<List<McpConnectionData>> {
        return try {
            val userId = currentUserId()
            val response = client.postgrest["mcp_connections"].select {
                filter { eq("user_id", userId) }
            }
            Result.Success(response.decodeList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load MCP connections")
        }
    }

    suspend fun saveMcpConnection(connection: McpConnectionData): Result<McpConnectionData> {
        return try {
            val userId = currentUserId()
            val saved = client.postgrest["mcp_connections"].upsert(
                mapOf(
                    "user_id" to userId,
                    "name" to connection.name,
                    "server_url" to connection.server_url,
                    "api_key" to connection.api_key,
                    "enabled" to connection.enabled,
                )
            ) {
                select()
            }.decodeSingle<McpConnectionData>()
            Result.Success(saved)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to save MCP connection")
        }
    }

    suspend fun deleteMcpConnection(connectionId: String): Result<Unit> {
        return try {
            client.postgrest["mcp_connections"].delete {
                filter { eq("id", connectionId) }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete MCP connection")
        }
    }
}
