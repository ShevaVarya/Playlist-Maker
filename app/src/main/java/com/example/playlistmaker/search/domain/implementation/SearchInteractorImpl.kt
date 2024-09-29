package com.example.playlistmaker.search.domain.implementation

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
            consumer.consume(trackRepository.searchTrack(text))
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