package com.example.playlistmaker.settings.data

import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.example.playlistmaker.App
import com.example.playlistmaker.common.Utils.SHARED_PREFERENCES_KEY_THEME
import com.example.playlistmaker.common.Utils.SHARED_PREFERENCES_NAME_FILE
import com.example.playlistmaker.settings.domain.api.SettingsRepository


class SettingsRepositoryImpl() : SettingsRepository {
    private var darkTheme = false
    private val sharedPreferences =
        App.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME_FILE, MODE_PRIVATE)


    override fun isNightTheme(): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY_THEME, darkTheme)
    }

    override fun saveTheme(isNightTheme: Boolean) {
        darkTheme = isNightTheme
        sharedPreferences.edit { putBoolean(SHARED_PREFERENCES_KEY_THEME, darkTheme) }
    }
}