package com.example.playlistmaker.settings.domain.api


interface SettingsInteractor {
    fun isNightTheme(): Boolean
    fun saveTheme(isNightTheme: Boolean)
}