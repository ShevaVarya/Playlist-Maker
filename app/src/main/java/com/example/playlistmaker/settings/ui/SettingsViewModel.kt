package com.example.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.models.IntentType

class SettingsViewModel(
    private val application: Context,
    private val sharingInteractor: SharingInteractor
) : AndroidViewModel(application as App) {


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
        return (application as App).darkTheme
    }

    fun switchTheme(isChecked: Boolean) {
        (application as App).switchTheme(isChecked)
    }
}