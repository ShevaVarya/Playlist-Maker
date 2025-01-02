package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SharedPreferencesRepository {
    fun saveInSharedPreferences(tracks: List<Track>)
    fun getFromSharedPreferences(): List<Track>
    fun clearSharedPreferences()
    fun addTrackToSharedPreferences(item: Track): List<Track>
    fun createJsonFromTracks(tracks: List<Track>): String
    fun createTracksFromJson(json: String?): List<Track>
}