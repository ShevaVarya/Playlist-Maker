package com.example.playlistmaker.domain.intents

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

interface SendEmail {
    fun sendEmail(context: Context, mail: String, message: String, subject: String)
}

class SendEmailImpl() : SendEmail {

    override fun sendEmail(context: Context, mail: String, message: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(context, intent, null)
    }
}