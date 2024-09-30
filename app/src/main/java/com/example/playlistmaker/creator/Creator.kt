package com.example.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.data.SharedPreferencesRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.implementation.SearchInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.workers.WorkerSharedPreferencesImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.implementation.SettingsInteractorImpl
import com.example.playlistmaker.sharing.data.workers.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.implementation.SharingInteractorImpl

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

    //
    fun provideSharingIntersctor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavagator(context))
    }

    private fun getExternalNavagator(context: Context): ExternalNavigator {
        return ExternalNavigator(context)
    }

    //
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(SettingsRepositoryImpl())
    }

    //
    fun providePlayerInteractor(mediaPlayer: MediaPlayer, trackUrl: String): PlayerInteractor {
        return PlayerInteractorImpl(mediaPlayer, trackUrl)
    }
}