package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.IntentType

interface SettingsRepository {
    fun makeIntent(intentType: IntentType)
    fun isNightTheme(): Boolean
    fun saveTheme(isNightTheme: Boolean)
}