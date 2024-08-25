package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SharedPreferencesRepository {
    fun saveInSharedPreferences(tracks: List<Track>)
    fun gerFromSharedPreferences(): List<Track>
    fun clearSharedPreferences()
    fun createJsonFromTracks(tracks: List<Track>): String
    fun createTracksFromJson(json: String?): List<Track>
}