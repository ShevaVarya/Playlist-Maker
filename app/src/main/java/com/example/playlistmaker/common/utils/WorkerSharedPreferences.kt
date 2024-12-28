package com.example.playlistmaker.common.utils

import android.content.SharedPreferences

interface WorkerSharedPreferences {
    fun saveInSharedPreferences(json: String)
    fun gerFromSharedPreferences(): String?
    fun clearSharedPreferences()
}

class WorkerSharedPreferencesImpl(
    private val sharedPreferences: SharedPreferences
) : WorkerSharedPreferences {

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

    private companion object {
        const val SHARED_PREFERENCES_TRACKS_KEY = "tracks_key"
    }
}