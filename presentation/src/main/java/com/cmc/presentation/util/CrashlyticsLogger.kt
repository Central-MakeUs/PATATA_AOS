package com.cmc.presentation.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

val crashlytics = FirebaseCrashlytics.getInstance()

object CrashlyticsLogger {
    private const val LAST_UI_ACTION = "last_ui_action"

    fun setLastUIAction(action: String) {
        crashlytics.setCustomKey(LAST_UI_ACTION, action)
    }

    fun setUser(id: String) {
        crashlytics.setUserId(id)
    }

    fun log(message: String) {
        crashlytics.log(message)
    }
}