package com.example.playlistmaker.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.ContextCompat.getString
import androidx.core.content.edit
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.common.constants.Utils.SHARED_PREFERENCES_KEY_THEME
import com.example.playlistmaker.common.constants.Utils.SHARED_PREFERENCES_NAME_FILE
import com.example.playlistmaker.data.workers.intents.OpenBrowser
import com.example.playlistmaker.data.workers.intents.SendEmail
import com.example.playlistmaker.data.workers.intents.SendMessage
import com.example.playlistmaker.domain.api.SettingsRepository


enum class IntentType {
    OPEN_BROWSER, SEND_EMAIL, SEND_MESSAGE
}

class SettingsRepositoryImpl(
    private val openBrowser: OpenBrowser,
    private val sendEmail: SendEmail,
    private val sendMessage: SendMessage,
    private val context: Context,
) : SettingsRepository {
    private companion object {
        val MAIL = getString(App.getContext(), R.string.mail)
        val EMAIL_MESSAGE = getString(App.getContext(), R.string.message_to_support)
        val EMAIL_SUBJECT = getString(App.getContext(), R.string.subject_of_message_to_support)
        val URL = getString(App.getContext(), R.string.url_to_user_agreement)
        val MESSAGE = getString(App.getContext(), R.string.url_practicum)
    }

    private var darkTheme = false
    private val sharedPreferences =
        App.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME_FILE, MODE_PRIVATE)


    override fun makeIntent(intentType: IntentType) {
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

    override fun isNightTheme(): Boolean {
        return sharedPreferences.getBoolean(SHARED_PREFERENCES_KEY_THEME, darkTheme)
    }

    override fun saveTheme(isNightTheme: Boolean) {
        darkTheme = isNightTheme
        sharedPreferences.edit { putBoolean(SHARED_PREFERENCES_KEY_THEME, darkTheme) }
    }
}