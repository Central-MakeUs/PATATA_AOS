package com.cmc.data.preferences

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.base.constants.DataStoreKeys.APP_PREFERENCES
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


val Context.appPreferences by preferencesDataStore(APP_PREFERENCES)

class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

}