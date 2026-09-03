package com.guidetradeai

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.guidetradeai.data.remote.SupabaseClient

class GuideTradeApp : Application() {
    val supabaseClient by lazy { SupabaseClient.client }

    val isUserAuthenticated: Boolean
        get() = SupabaseClient.client.auth.currentSessionOrNull() != null

    override fun onCreate() {
        super.onCreate()
    }

    fun getSharedPreferences(): SharedPreferences {
        return getSharedPreferences("guidetradeai_prefs", Context.MODE_PRIVATE)
    }
}
