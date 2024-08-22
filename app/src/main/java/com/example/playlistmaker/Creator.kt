package com.example.playlistmaker

import com.example.playlistmaker.data.TrackRepositoryImplementation
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.implementation.TrackInteractorImplementation

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImplementation(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImplementation(getTrackRepository())
    }
}