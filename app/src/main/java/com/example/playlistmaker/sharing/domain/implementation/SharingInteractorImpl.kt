package com.example.playlistmaker.sharing.domain.implementation

import com.example.playlistmaker.sharing.data.workers.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.models.IntentType

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun sendEmail() {
        externalNavigator.makeIntent(IntentType.SEND_EMAIL)
    }

    override fun openBrowser() {
        externalNavigator.makeIntent(IntentType.OPEN_BROWSER)
    }

    override fun sendMessage() {
        externalNavigator.makeIntent(IntentType.SEND_MESSAGE)
    }
}