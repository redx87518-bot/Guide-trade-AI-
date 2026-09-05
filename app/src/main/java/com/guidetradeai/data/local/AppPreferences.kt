package com.guidetradeai.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("app_prefs")

class AppPreferences(private val context: Context) {
    companion object {
        val LAST_SESSION_ID = stringPreferencesKey("last_session_id")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val AUTO_SPEAK = booleanPreferencesKey("auto_speak")
    }

    val lastSessionId: Flow<String?> = context.dataStore.data
        .map { it[LAST_SESSION_ID] }

    suspend fun saveLastSessionId(id: String) {
        context.dataStore.edit { it[LAST_SESSION_ID] = id }
    }

    suspend fun clearLastSessionId() {
        context.dataStore.edit { it.remove(LAST_SESSION_ID) }
    }

    val voiceEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[VOICE_ENABLED] ?: true }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[VOICE_ENABLED] = enabled }
    }

    val autoSpeak: Flow<Boolean> = context.dataStore.data
        .map { it[AUTO_SPEAK] ?: false }

    suspend fun setAutoSpeak(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_SPEAK] = enabled }
    }
}
