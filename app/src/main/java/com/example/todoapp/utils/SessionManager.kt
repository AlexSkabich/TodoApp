package com.example.todoapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// This creates a single DataStore instance named "app_prefs"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

@Singleton
class SessionManager @Inject constructor( // <-- MUST BE SessionManager to match the file name
    @ApplicationContext private val context: Context
) {
    companion object {
        // Auth Keys
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val CURRENT_USER_EMAIL = stringPreferencesKey("current_user_email")

        // Settings Keys
        val TEXT_COLOR = stringPreferencesKey("text_color")
        val BG_COLOR = stringPreferencesKey("bg_color")
        val FONT_FAMILY = stringPreferencesKey("font_family")
    }

    // --- Auth Flows ---
    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[CURRENT_USER_ID] }
    val userEmailFlow: Flow<String?> = context.dataStore.data.map { it[CURRENT_USER_EMAIL] }

    suspend fun saveSession(userId: String, email: String) {
        context.dataStore.edit {
            it[CURRENT_USER_ID] = userId
            it[CURRENT_USER_EMAIL] = email
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit {
            it.remove(CURRENT_USER_ID)
            it.remove(CURRENT_USER_EMAIL)
        }
    }

    // --- Settings Flows ---
    val textColorFlow: Flow<String> = context.dataStore.data.map { it[TEXT_COLOR] ?: "DEFAULT" }
    val bgColorFlow: Flow<String> = context.dataStore.data.map { it[BG_COLOR] ?: "DEFAULT_BG" }
    val fontFamilyFlow: Flow<String> = context.dataStore.data.map { it[FONT_FAMILY] ?: "DEFAULT" }

    suspend fun saveSettings(textColor: String, bgColor: String, fontFamily: String) {
        context.dataStore.edit {
            it[TEXT_COLOR] = textColor
            it[BG_COLOR] = bgColor
            it[FONT_FAMILY] = fontFamily
        }
    }

    suspend fun resetSettings() {
        context.dataStore.edit {
            it.remove(TEXT_COLOR)
            it.remove(BG_COLOR)
            it.remove(FONT_FAMILY)
        }
    }
}