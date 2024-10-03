package com.example.playlistmaker.sharing.data.workers

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.workers.intents.OpenBrowser
import com.example.playlistmaker.sharing.data.workers.intents.SendEmail
import com.example.playlistmaker.sharing.data.workers.intents.SendMessage
import com.example.playlistmaker.sharing.domain.models.IntentType

class ExternalNavigator(
    private val context: Context,
    private val sendEmail: SendEmail,
    private val openBrowser: OpenBrowser,
    private val sendMessage: SendMessage
) {

    fun makeIntent(intentType: IntentType) {
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

    private companion object {
        val MAIL = getString(App.getContext(), R.string.mail)
        val EMAIL_MESSAGE = getString(App.getContext(), R.string.message_to_support)
        val EMAIL_SUBJECT = getString(App.getContext(), R.string.subject_of_message_to_support)
        val URL = getString(App.getContext(), R.string.url_to_user_agreement)
        val MESSAGE = getString(App.getContext(), R.string.url_practicum)
    }
}