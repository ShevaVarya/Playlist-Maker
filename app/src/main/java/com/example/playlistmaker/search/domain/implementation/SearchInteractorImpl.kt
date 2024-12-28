package com.example.playlistmaker.search.domain.implementation

import com.example.playlistmaker.common.utils.Resource
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val trackRepository: TrackRepository
) : SearchInteractor {

    override fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>> {
        return trackRepository.searchTrack(text).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addTrackToSharedPreferences(item: Track): List<Track> {
        return sharedPreferencesRepository.addTrackToSharedPreferences(item)
    }

    override fun saveInSharedPreferences(tracks: List<Track>) {
        sharedPreferencesRepository.saveInSharedPreferences(tracks)
    }

    override fun getFromSharedPreferences(): List<Track> {
        return sharedPreferencesRepository.getFromSharedPreferences()
    }

    override fun clearSharedPreferences() {
        sharedPreferencesRepository.clearSharedPreferences()
    }
}