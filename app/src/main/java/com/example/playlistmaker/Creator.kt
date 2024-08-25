package com.example.playlistmaker

import com.example.playlistmaker.data.SharedPreferencesRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.workers.WorkerSharedPreferencesImpl
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.implementation.SearchInteractorImpl
import com.example.playlistmaker.domain.implementation.SettingsInteractorImpl
import com.example.playlistmaker.domain.intents.OpenBrowserImpl
import com.example.playlistmaker.domain.intents.SendEmailImpl
import com.example.playlistmaker.domain.intents.SendMessageImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    private fun getSharedPreferencesRepository(): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(WorkerSharedPreferencesImpl())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSharedPreferencesRepository(), getTrackRepository())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(OpenBrowserImpl(), SendEmailImpl(), SendMessageImpl())
    }

}