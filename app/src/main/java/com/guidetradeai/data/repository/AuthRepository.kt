package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun signUp(email: String, password: String, fullName: String? = null): Result<Unit> {
        return try {
            supabase.signUp(email, password, fullName)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign up failed")
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase.signIn(email, password)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sign in failed")
        }
    }

    suspend fun signOut() {
        supabase.signOut()
    }

    fun isLoggedIn(): Boolean {
        return supabase.currentUser() != null
    }

    fun currentUserId(): String {
        return supabase.currentUserId()
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return supabase.resetPassword(email)
    }
}
