package com.cmc.data.base.constants

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cmc.common.constants.PreferenceKeys

object DataStoreKeys {
    const val AUTH_PREFERENCES_DATASTORE = "auth_prefs"
    const val USER_DATASTORE = "user_data"

    val KEY_ACCESS_TOKEN = stringPreferencesKey(PreferenceKeys.KEY_ACCESS_TOKEN)
    val KEY_REFRESH_TOKEN = stringPreferencesKey(PreferenceKeys.KEY_REFRESH_TOKEN)

    val KEY_USER_ID = stringPreferencesKey(PreferenceKeys.KEY_USER_ID)

    val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey(PreferenceKeys.KEY_ONBOARDING_COMPLETED)
}