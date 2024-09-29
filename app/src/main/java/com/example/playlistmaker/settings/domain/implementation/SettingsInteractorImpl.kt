package com.example.playlistmaker.settings.domain.implementation

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository


class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun isNightTheme(): Boolean {
        return settingsRepository.isNightTheme()
    }

    override fun saveTheme(isNightTheme: Boolean) {
        settingsRepository.saveTheme(isNightTheme)
    }
}