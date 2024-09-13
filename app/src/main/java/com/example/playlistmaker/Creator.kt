package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.SettingsRepositoryImpl
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
import com.example.playlistmaker.data.workers.intents.OpenBrowserImpl
import com.example.playlistmaker.data.workers.intents.SendEmailImpl
import com.example.playlistmaker.data.workers.intents.SendMessageImpl
import com.example.playlistmaker.domain.api.SettingsRepository

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

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(OpenBrowserImpl(), SendEmailImpl(), SendMessageImpl(), context)
    }
}