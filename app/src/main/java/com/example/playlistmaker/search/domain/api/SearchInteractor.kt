package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun searchTracks(text: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: Pair<Int,List<Track>>)
    }

    fun saveInSharedPreferences(tracks: List<Track>)
    fun gerFromSharedPreferences(): List<Track>
    fun clearSharedPreferences()
}