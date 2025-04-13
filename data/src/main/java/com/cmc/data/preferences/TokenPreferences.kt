package com.cmc.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.base.constants.DataStoreKeys.TOKEN_PREFERENCES
import com.cmc.data.base.constants.DataStoreKeys.KEY_ACCESS_TOKEN
import com.cmc.data.base.constants.DataStoreKeys.KEY_GOOGLE_ACCESS_TOKEN
import com.cmc.data.base.constants.DataStoreKeys.KEY_REFRESH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// DataStore 생성
val Context.tokenPreferences by preferencesDataStore(TOKEN_PREFERENCES)

class TokenPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null
    private var cachedGoogleAccessToken: String? = null

    fun getCachedAccessToken(): String? {
        return cachedAccessToken
    }

    fun getCachedRefreshToken(): String? {
        return cachedRefreshToken
    }

    fun getCachedGoogleAccessToken(): String? {
        return cachedGoogleAccessToken
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
        context.tokenPreferences.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        cachedAccessToken = accessToken
        context.tokenPreferences.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveGoogleAccessToken(googleAccessToken: String) {
        cachedGoogleAccessToken = googleAccessToken
        context.tokenPreferences.edit { preferences ->
            preferences[KEY_GOOGLE_ACCESS_TOKEN] = googleAccessToken
        }
    }

    suspend fun getAccessToken(): String? {
        if (cachedAccessToken == null) {
            cachedAccessToken = context.tokenPreferences.data
                .map { preferences -> preferences[KEY_ACCESS_TOKEN] }
                .first()
        }
        return cachedAccessToken
    }

    suspend fun getRefreshToken(): String? {
        if (cachedRefreshToken == null) {
            cachedRefreshToken = context.tokenPreferences.data
                .map { preferences -> preferences[KEY_REFRESH_TOKEN] }
                .first()
        }
        return cachedRefreshToken
    }

    suspend fun getGoogleAccessToken(): String? {
        if (cachedGoogleAccessToken == null) {
            cachedGoogleAccessToken = context.tokenPreferences.data
                .map { preferences -> preferences[KEY_GOOGLE_ACCESS_TOKEN] }
                .first()
        }
        return cachedGoogleAccessToken
    }

    suspend fun clearTokens() {
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedGoogleAccessToken = null
        context.tokenPreferences.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
            preferences.remove(KEY_GOOGLE_ACCESS_TOKEN)
        }
    }
}
