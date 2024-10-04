package com.example.playlistmaker.sharing.data.workers.intents

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

interface SendMessage {
    fun sendMessage(context: Context, value: String)
}

class SendMessageImpl : SendMessage {

    override fun sendMessage(context: Context, value: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/*"
            putExtra(Intent.EXTRA_TEXT, value)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(context, intent, null)
    }
}