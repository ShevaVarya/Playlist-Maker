package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun searchTracks(text: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

    fun saveInSharedPreferences(tracks: List<Track>)
    fun getFromSharedPreferences(): List<Track>
    fun clearSharedPreferences()
}