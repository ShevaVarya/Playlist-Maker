package com.example.playlistmaker.settings.ui

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.models.IntentType

class SettingsViewModel(
    private val application: App
) : AndroidViewModel(application) {

    private val sharingInteractor = Creator.provideSharingIntersctor()

    fun makeIntent(intentType: IntentType) {
        when (intentType) {
            IntentType.OPEN_BROWSER -> {
                sharingInteractor.openBrowser()
            }

            IntentType.SEND_EMAIL -> {
                sharingInteractor.sendEmail()
            }

            IntentType.SEND_MESSAGE -> {
                sharingInteractor.sendMessage()
            }
        }
    }

    fun isChecked(): Boolean {
        return application.darkTheme
    }

    fun switchTheme(isChecked: Boolean) {
        application.switchTheme(isChecked)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                SettingsViewModel(application)
            }
        }
    }
}