package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthRepository(
    private val supabase: SupabaseClient,
) {
    val sessionStatus: Flow<SessionStatus> = supabase.auth.sessionStatus

    fun getCurrentUser(): User? {
        val u = supabase.auth.currentUserOrNull() ?: return null
        return User(
            id = u.id,
            email = u.email ?: "",
            fullName = u.userMetadata?.jsonObject?.get("full_name")?.jsonPrimitive?.content,
            avatarUrl = u.userMetadata?.jsonObject?.get("avatar_url")?.jsonPrimitive?.content,
            createdAt = u.createdAt?.toString() ?: "",
            updatedAt = u.updatedAt?.toString() ?: "",
        )
    }

    suspend fun signUp(email: String, password: String, fullName: String): Result<User> {
        return try {
            val data = buildJsonObject { put("full_name", JsonPrimitive(fullName)) }
            val result = supabase.auth.signUpWith(
                Email,
                password,
            ) {
                this.email = email
                this.data = data
            }
            val u = result
            if (u != null) {
                Result.success(
                    User(
                        id = u.id,
                        email = u.email ?: "",
                        fullName = fullName,
                    ),
                )
            } else {
                Result.error("Sign up succeeded but no user returned. Please check your email to verify your account.")
            }
        } catch (e: Exception) {
            Result.error(mapAuthError(e.message ?: "Sign up failed"))
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            supabase.auth.signInWith(
                Email,
                password,
            ) {
                this.email = email
            }
            val u = supabase.auth.currentUserOrNull()
            if (u != null) {
                Result.success(
                    User(
                        id = u.id,
                        email = u.email ?: "",
                        fullName = u.userMetadata?.jsonObject?.get("full_name")?.jsonPrimitive?.content ?: "",
                        avatarUrl = u.userMetadata?.jsonObject?.get("avatar_url")?.jsonPrimitive?.content,
                        createdAt = u.createdAt?.toString() ?: "",
                        updatedAt = u.updatedAt?.toString() ?: "",
                    ),
                )
            } else {
                Result.error("Login succeeded but no user returned")
            }
        } catch (e: Exception) {
            Result.error(mapAuthError(e.message ?: "Login failed"))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(mapAuthError(e.message ?: "Logout failed"))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(mapAuthError(e.message ?: "Failed to send reset email"))
        }
    }

    suspend fun updateProfile(fullName: String?, avatarUrl: String?): Result<User> {
        return try {
            supabase.auth.updateUser {
                data = buildJsonObject {
                    fullName?.let { put("full_name", JsonPrimitive(it)) }
                    avatarUrl?.let { put("avatar_url", JsonPrimitive(it)) }
                }
            }
            val u = supabase.auth.currentUserOrNull()
            if (u != null) {
                Result.success(
                    User(
                        id = u.id,
                        email = u.email ?: "",
                        fullName = fullName ?: u.userMetadata?.jsonObject?.get("full_name")?.jsonPrimitive?.content ?: "",
                        avatarUrl = avatarUrl ?: u.userMetadata?.jsonObject?.get("avatar_url")?.jsonPrimitive?.content,
                        createdAt = u.createdAt?.toString() ?: "",
                        updatedAt = u.updatedAt?.toString() ?: "",
                    ),
                )
            } else {
                Result.error("No user found after update")
            }
        } catch (e: Exception) {
            Result.error(mapAuthError(e.message ?: "Failed to update profile"))
        }
    }

    fun isUserAuthenticated(): Boolean {
        return supabase.auth.currentSessionOrNull() != null
    }

    private fun mapAuthError(message: String): String {
        return when {
            message.contains("Invalid login credentials", ignoreCase = true) -> "Invalid email or password."
            message.contains("already been registered", ignoreCase = true) -> "This email is already registered."
            message.contains("rate limit", ignoreCase = true) -> "Too many attempts. Please try again later."
            message.contains("weak password", ignoreCase = true) -> "Password is too weak. Use at least 8 characters."
            message.contains("verify", ignoreCase = true) -> "Please verify your email before logging in."
            message.contains("not confirmed", ignoreCase = true) -> "Please verify your email before logging in."
            message.contains("email", ignoreCase = true) -> "Please verify your email before logging in."
            else -> message
        }
    }
}
