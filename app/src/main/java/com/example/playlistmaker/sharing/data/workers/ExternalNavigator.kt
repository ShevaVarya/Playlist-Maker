package com.example.playlistmaker.sharing.data.workers

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.workers.intents.SendEmailImpl
import com.example.playlistmaker.sharing.domain.models.IntentType
import com.example.playlistmaker.sharing.data.workers.intents.OpenBrowserImpl
import com.example.playlistmaker.sharing.data.workers.intents.SendMessageImpl

class ExternalNavigator(private val context: Context) {

    private companion object {
        val MAIL = getString(App.getContext(), R.string.mail)
        val EMAIL_MESSAGE = getString(App.getContext(), R.string.message_to_support)
        val EMAIL_SUBJECT = getString(App.getContext(), R.string.subject_of_message_to_support)
        val URL = getString(App.getContext(), R.string.url_to_user_agreement)
        val MESSAGE = getString(App.getContext(), R.string.url_practicum)
    }

    private val sendEmail = SendEmailImpl()
    private val openBrowser = OpenBrowserImpl()
    private val sendMessage = SendMessageImpl()

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
}