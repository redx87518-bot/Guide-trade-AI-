package com.guidetradeai.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.compose.ui.unit.sp

val Context.dataStore by preferencesDataStore("app_prefs")

class AppPreferences(private val context: Context) {
    private val LAST_SESSION_ID = stringPreferencesKey("last_session_id")

    val lastSessionId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[LAST_SESSION_ID]
    }

    val voiceEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[stringPreferencesKey("voice_enabled")]?.toBooleanStrictOrNull() ?: true
    }

    val autoSpeak: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[stringPreferencesKey("auto_speak")]?.toBooleanStrictOrNull() ?: false
    }

    suspend fun saveLastSessionId(sessionId: String) {
        context.dataStore.edit { prefs -> prefs[LAST_SESSION_ID] = sessionId }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[stringPreferencesKey("voice_enabled")] = enabled.toString() }
    }

    suspend fun setAutoSpeak(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[stringPreferencesKey("auto_speak")] = enabled.toString() }
    }
}
