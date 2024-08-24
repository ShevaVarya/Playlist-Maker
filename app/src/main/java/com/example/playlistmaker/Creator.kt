package com.example.playlistmaker

import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.implementation.SettingsInteractorImpl
import com.example.playlistmaker.domain.implementation.TrackInteractorImpl
import com.example.playlistmaker.domain.intents.OpenBrowserImpl
import com.example.playlistmaker.domain.intents.SendEmailImpl
import com.example.playlistmaker.domain.intents.SendMessageImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(OpenBrowserImpl(), SendEmailImpl(), SendMessageImpl())
    }

}