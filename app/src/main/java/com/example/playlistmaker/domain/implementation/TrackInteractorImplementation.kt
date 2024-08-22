package com.example.playlistmaker.domain.implementation

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository

class TrackInteractorImplementation(private val repository: TrackRepository) : TrackInteractor {
    override fun searchTracks(text: String, consumer: TrackInteractor.TrackConsumer) {
        val thread = Thread {
            consumer.consume(repository.searchTrack(text))
        }
        thread.start()
    }
}