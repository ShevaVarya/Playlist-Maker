package com.example.playlistmaker.domain.implementation

import com.example.playlistmaker.data.IntentType
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository


class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun makeIntent(intentType: IntentType) {
        settingsRepository.makeIntent(intentType)
    }

    override fun isNightTheme(): Boolean {
        return settingsRepository.isNightTheme()
    }

    override fun saveTheme(isNightTheme: Boolean) {
        settingsRepository.saveTheme(isNightTheme)
    }
}