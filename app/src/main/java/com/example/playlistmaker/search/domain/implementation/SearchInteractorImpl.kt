package com.example.playlistmaker.search.domain.implementation

import com.example.playlistmaker.common.util.Resource
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchInteractorImpl(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val trackRepository: TrackRepository
) : SearchInteractor {

    override fun searchTracks(text: String, consumer: SearchInteractor.TrackConsumer) {
        val thread = Thread {
            when(val resource = trackRepository.searchTrack(text)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }
                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
        thread.start()
    }

    override fun saveInSharedPreferences(tracks: List<Track>){
        sharedPreferencesRepository.saveInSharedPreferences(tracks)
    }

    override fun gerFromSharedPreferences(): List<Track> {
        return sharedPreferencesRepository.gerFromSharedPreferences()
    }

    override fun clearSharedPreferences() {
        sharedPreferencesRepository.clearSharedPreferences()
    }
}