package com.example.playlistmaker.domain.api

import android.content.Context
import com.example.playlistmaker.data.IntentType

interface SettingsInteractor {
    fun makeIntent(intentType: IntentType)
    fun isNightTheme(): Boolean
    fun saveTheme(isNightTheme: Boolean)
}