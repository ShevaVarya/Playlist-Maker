package com.example.playlistmaker.settings.domain.api


interface SettingsRepository {
    fun isNightTheme(): Boolean
    fun saveTheme(isNightTheme: Boolean)
}