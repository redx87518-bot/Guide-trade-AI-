package com.guidetradeai.data.repository

import com.guidetradeai.data.remote.SupabaseClientWrapper
import com.guidetradeai.domain.Result
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val supabase: SupabaseClientWrapper = SupabaseClientWrapper) {

    suspend fun signUp(email: String, password: String, fullName: String? = null): Result<Unit> {
        return supabase.signUp(email, password, fullName).map { }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return supabase.signIn(email, password).map { }
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
