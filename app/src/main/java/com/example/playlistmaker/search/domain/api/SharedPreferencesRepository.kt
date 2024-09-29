package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SharedPreferencesRepository {
    fun saveInSharedPreferences(tracks: List<Track>)
    fun gerFromSharedPreferences(): List<Track>
    fun clearSharedPreferences()
    fun createJsonFromTracks(tracks: List<Track>): String
    fun createTracksFromJson(json: String?): List<Track>
}