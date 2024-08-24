package com.example.playlistmaker.domain.api

import android.content.Context
import com.example.playlistmaker.domain.implementation.IntentType

interface SettingsInteractor {
    fun makeIntent(intentType: IntentType, context: Context)
}