package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClient
import com.guidetradeai.domain.Result
import io.github.jan.supabase.gotrue.providers.builtin.Email

class AuthRepository(private val supabase: SupabaseClient = SupabaseClient) {

    suspend fun signUp(email: String, password: String, fullName: String? = null): Result<Unit> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                fullName?.let {
                    data = kotlinx.serialization.json.buildJsonObject {
                        put("full_name", kotlinx.serialization.json.JsonPrimitive(it))
                    }
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign up failed")
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign in failed")
        }
    }

    suspend fun signOut() {
        try { supabase.auth.signOut() } catch (_: Exception) {}
    }

    fun isLoggedIn(): Boolean = supabase.auth.currentSessionOrNull() != null
    fun currentUserId(): String = supabase.auth.currentUserOrNull()?.id ?: ""

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Password reset failed")
        }
    }
}
