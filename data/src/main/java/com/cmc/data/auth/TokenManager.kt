package com.cmc.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// DataStore 생성
val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) : TokenStorage {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun getAccessToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[KEY_ACCESS_TOKEN] }
            .first()
    }

    override suspend fun getRefreshToken(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[KEY_REFRESH_TOKEN] }
            .first()
    }

    override suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
        }
    }
}
