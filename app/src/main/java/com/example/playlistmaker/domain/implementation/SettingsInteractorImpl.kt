package com.example.playlistmaker.domain.implementation

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.R
import com.example.playlistmaker.common.util.App
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.intents.OpenBrowser
import com.example.playlistmaker.domain.intents.SendEmail
import com.example.playlistmaker.domain.intents.SendMessage

enum class IntentType {
    OPEN_BROWSER,
    SEND_EMAIL,
    SEND_MESSAGE
}

class SettingsInteractorImpl(
    private val openBrowser: OpenBrowser,
    private val sendEmail: SendEmail,
    private val sendMessage: SendMessage,
) : SettingsInteractor {

    private companion object {
        val MAIL = getString(App.getContext(), R.string.mail)
        val EMAIL_MESSAGE = getString(App.getContext(), R.string.message_to_support)
        val EMAIL_SUBJECT = getString(App.getContext(), R.string.subject_of_message_to_support)
        val URL = getString(App.getContext(), R.string.url_to_user_agreement)
        val MESSAGE = getString(App.getContext(), R.string.url_practicum)
    }

    override fun makeIntent(intentType: IntentType, context: Context) {
        when (intentType) {
            IntentType.SEND_EMAIL -> {
                sendEmail.sendEmail(context, MAIL, EMAIL_MESSAGE, EMAIL_SUBJECT)
            }

            IntentType.OPEN_BROWSER -> {
                openBrowser.openBrowser(context, URL)
            }

            IntentType.SEND_MESSAGE -> {
                sendMessage.sendMessage(context, MESSAGE)
            }
        }
    }
}