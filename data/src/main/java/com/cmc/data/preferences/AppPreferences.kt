package com.cmc.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.base.constants.DataStoreKeys.APP_PREFERENCES
import com.cmc.data.base.constants.DataStoreKeys.KEY_ONBOARDING_COMPLETED
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject


val Context.appPreferences by preferencesDataStore(APP_PREFERENCES)

class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.appPreferences.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return context.appPreferences.data.first()[KEY_ONBOARDING_COMPLETED] ?: false
    }
}