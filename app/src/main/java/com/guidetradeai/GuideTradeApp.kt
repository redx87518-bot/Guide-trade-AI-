package com.guidetradeai

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.guidetradeai.data.local.AppPreferences
import com.guidetradeai.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth

class GuideTradeApp : Application() {
    val supabaseClient by lazy { SupabaseClient.client }

    val isUserAuthenticated: Boolean
        get() = SupabaseClient.client.auth.currentSessionOrNull() != null

    override fun onCreate() {
        super.onCreate()
        com.guidetradeai.di.AppModule.applicationContext = applicationContext
        com.guidetradeai.di.AppModule.appPreferences = AppPreferences(applicationContext)
    }

    fun getSharedPreferences(): SharedPreferences {
        return getSharedPreferences("guidetradeai_prefs", Context.MODE_PRIVATE)
    }
}
