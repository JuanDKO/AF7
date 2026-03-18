package com.example.af7.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AF7_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_NOTIFS_ENABLED = "key_notifs_enabled"
        private const val KEY_USERNAME = "key_username"
    }

    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var areNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFS_ENABLED, value).apply()

    var username: String
        get() = prefs.getString(KEY_USERNAME, "Usuario") ?: "Usuario"
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()
}
