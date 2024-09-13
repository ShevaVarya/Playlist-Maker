package com.example.playlistmaker.data.workers.intents

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

interface OpenBrowser {
    fun openBrowser(context: Context, url: String)
}

class OpenBrowserImpl() : OpenBrowser {

    override fun openBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(context, intent, null)
    }
}