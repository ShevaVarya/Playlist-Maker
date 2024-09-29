package com.example.playlistmaker.settings.data.workers

import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.App
import com.example.playlistmaker.common.Utils.SHARED_PREFERENCES_NAME_FILE

interface WorkerSharedPreferences {
    fun saveInSharedPreferences(json: String)
    fun gerFromSharedPreferences(): String?
    fun clearSharedPreferences()
}

class WorkerSharedPreferencesImpl() : WorkerSharedPreferences {

    private companion object {
        val SHARED_PREFERENCES_TRACKS_KEY = "tracks_key"
    }

    private val sharedPreferences =
        App.getContext().getSharedPreferences(SHARED_PREFERENCES_NAME_FILE, MODE_PRIVATE)

    override fun saveInSharedPreferences(json: String) {
        sharedPreferences.edit()
            .putString(SHARED_PREFERENCES_TRACKS_KEY, json)
            .apply()
    }

    override fun gerFromSharedPreferences(): String? {
        return sharedPreferences.getString(SHARED_PREFERENCES_TRACKS_KEY, null)
    }

    override fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}