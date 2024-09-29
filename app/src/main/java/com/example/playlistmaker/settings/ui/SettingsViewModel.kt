package com.example.playlistmaker.settings.ui

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.models.IntentType

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val application: App
) : AndroidViewModel(application) {

    companion object {
        fun getViewModelFactory(
            sharingInteractor: SharingInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                SettingsViewModel(sharingInteractor, application)
            }

        }
    }

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
}