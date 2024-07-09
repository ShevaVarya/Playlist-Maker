package com.example.playlistmaker.instruments

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val SHARED_PREFERENCES_NAME_FILE = "SharedPreferencePlaylistMaker"
const val SHARED_PREFERENCES_KEY = "shared_preferences_key"

class App : Application() {
    var darkTheme = false
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME_FILE, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY, darkTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPreferences.edit()
            .putBoolean(SHARED_PREFERENCES_KEY, darkThemeEnabled)
            .apply()
    }
}