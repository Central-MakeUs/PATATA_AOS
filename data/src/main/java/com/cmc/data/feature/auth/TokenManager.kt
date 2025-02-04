package com.cmc.data.feature.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.base.constants.DataStoreKeys.AUTH_PREFERENCES_DATASTORE
import com.cmc.data.base.constants.DataStoreKeys.KEY_ACCESS_TOKEN
import com.cmc.data.base.constants.DataStoreKeys.KEY_REFRESH_TOKEN
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// DataStore 생성
val Context.dataStore by preferencesDataStore(name = AUTH_PREFERENCES_DATASTORE)

class TokenManager(private val context: Context) : TokenStorage {

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun saveAccessToken(accessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
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
