package com.guidetradeai.data.repository

import android.util.Log
import com.guidetradeai.domain.Result
import com.guidetradeai.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
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
            val result = supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
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
                Result.success(
                    User(
                        id = "pending_verification",
                        email = email,
                        fullName = fullName,
                    ),
                )
            }
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "signUp failed", e)
            Result.error(mapAuthError(e.message ?: "Sign up failed"))
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
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
            Log.e("SupabaseAuth", "signIn failed", e)
            Result.error(mapAuthError(e.message ?: "Login failed"))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "signOut failed", e)
            Result.error(mapAuthError(e.message ?: "Logout failed"))
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "resetPassword failed", e)
            Result.error(mapAuthError(e.message ?: "Failed to send reset email"))
        }
    }

    suspend fun resendVerificationEmail(email: String): Result<Unit> {
        return try {
            supabase.auth.resendEmail(OtpType.Email.SIGNUP, email)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "resendVerificationEmail failed", e)
            Result.error(mapAuthError(e.message ?: "Failed to resend verification email"))
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
            Log.e("SupabaseAuth", "updateProfile failed", e)
            Result.error(mapAuthError(e.message ?: "Failed to update profile"))
        }
    }

    fun isUserAuthenticated(): Boolean {
        return supabase.auth.currentSessionOrNull() != null
    }

    private fun mapAuthError(message: String): String {
        Log.w("SupabaseAuth", "Mapping auth error: $message")
        return when {
            message.contains("Invalid login credentials", ignoreCase = true) -> "Invalid email or password."
            message.contains("already been registered", ignoreCase = true) -> "This email is already registered."
            message.contains("rate limit", ignoreCase = true) -> "Too many attempts. Please try again later."
            message.contains("weak password", ignoreCase = true) -> "Password is too weak. Use at least 8 characters."
            message.contains("not confirmed", ignoreCase = true) -> "Please verify your email before logging in."
            message.contains("verify", ignoreCase = true) -> "Please verify your email before logging in."
            else -> message
        }
    }
}
