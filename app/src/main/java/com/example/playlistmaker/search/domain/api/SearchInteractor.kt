package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>>

    fun saveInSharedPreferences(tracks: List<Track>)
    fun getFromSharedPreferences(): List<Track>
    fun clearSharedPreferences()
}