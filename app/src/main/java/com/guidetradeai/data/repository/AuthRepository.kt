package com.guidetradeai.data.repository

import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.UpdateUserData
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.exception.AuthException
import io.github.jan.supabase.gotrue.status.SessionStatus
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
            fullName = u.userMetadata?.jsonObject?.get("full_name")?.jsonPrimitive?.contentOrNull,
            avatarUrl = u.userMetadata?.jsonObject?.get("avatar_url")?.jsonPrimitive?.contentOrNull,
            createdAt = u.createdAt?.toString() ?: "",
            updatedAt = u.updatedAt?.toString() ?: "",
        )
    }

    suspend fun signUp(email: String, password: String, fullName: String): Result<User> {
        return try {
            val data = buildJsonObject { put("full_name", JsonPrimitive(fullName)) }
            val result = supabase.auth.signUpWith(
                io.github.jan.supabase.gotrue.SignUpAuthData(
                    email = email,
                    password = password,
                    data = data,
                ),
            )
            val u = result.user
            if (u != null) {
                Result.success(
                    User(
                        id = u.id,
                        email = u.email ?: "",
                        fullName = fullName,
                    ),
                )
            } else {
                Result.error("Sign up succeeded but no user returned")
            }
        } catch (e: AuthException) {
            Result.error(mapAuthError(e.message ?: "Sign up failed"))
        } catch (e: Exception) {
            Result.error("Sign up failed: ${e.message}")
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = supabase.auth.signInWith(
                io.github.jan.supabase.gotrue.SignInWithPasswordAuthData(
                    email = email,
                    password = password,
                ),
            )
            val u = result.user
            if (u != null) {
                Result.success(
                    User(
                        id = u.id,
                        email = u.email ?: "",
                        fullName = u.userMetadata?.jsonObject?.get("full_name")?.jsonPrimitive?.contentOrNull ?: "",
                        avatarUrl = u.userMetadata?.jsonObject?.get("avatar_url")?.jsonPrimitive?.contentOrNull,
                        createdAt = u.createdAt?.toString() ?: "",
                        updatedAt = u.updatedAt?.toString() ?: "",
                    ),
                )
            } else {
                Result.error("Login succeeded but no user returned")
            }
        } catch (e: AuthException) {
            Result.error(mapAuthError(e.message ?: "Login failed"))
        } catch (e: Exception) {
            Result.error("Login failed: ${e.message}")
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: AuthException) {
            Result.error(mapAuthError(e.message ?: "Logout failed"))
        } catch (e: Exception) {
            Result.error("Logout failed: ${e.message}")
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: AuthException) {
            Result.error(mapAuthError(e.message ?: "Failed to send reset email"))
        } catch (e: Exception) {
            Result.error("Failed to send reset email: ${e.message}")
        }
    }

    suspend fun updateProfile(fullName: String?, avatarUrl: String?): Result<User> {
        return try {
            val data = buildJsonObject {
                fullName?.let { put("full_name", JsonPrimitive(it)) }
                avatarUrl?.let { put("avatar_url", JsonPrimitive(it)) }
            }
            supabase.auth.updateUser(
                UpdateUserData(
                    data = data,
                ),
            )
            val u = supabase.auth.currentUserOrNull()
            if (u != null) {
                Result.success(
                    User(
                        id = u.id,
                        email = u.email ?: "",
                        fullName = fullName ?: u.userMetadata?.jsonObject?.get("full_name")?.jsonPrimitive?.contentOrNull ?: "",
                        avatarUrl = avatarUrl ?: u.userMetadata?.jsonObject?.get("avatar_url")?.jsonPrimitive?.contentOrNull,
                        createdAt = u.createdAt?.toString() ?: "",
                        updatedAt = u.updatedAt?.toString() ?: "",
                    ),
                )
            } else {
                Result.error("No user found after update")
            }
        } catch (e: AuthException) {
            Result.error(mapAuthError(e.message ?: "Failed to update profile"))
        } catch (e: Exception) {
            Result.error("Failed to update profile: ${e.message}")
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
            else -> message
        }
    }
}
