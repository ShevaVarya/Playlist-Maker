package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.common.utils.Utils.SHARED_PREFERENCES_KEY_THEME
import com.example.playlistmaker.settings.domain.api.SettingsRepository


class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {
    private var darkTheme = false


    override fun isNightTheme(): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY_THEME, darkTheme)
    }

    override fun saveTheme(isNightTheme: Boolean) {
        darkTheme = isNightTheme
        sharedPreferences.edit { putBoolean(SHARED_PREFERENCES_KEY_THEME, darkTheme) }
    }
}