package com.example.playlistmaker.common.di

import com.example.playlistmaker.media.data.CreatePlaylistRepositoryImpl
import com.example.playlistmaker.media.data.FavouriteTracksRepositoryImpl
import com.example.playlistmaker.media.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.media.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.data.SharedPreferencesRepositoryImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single<TrackRepository> {
        TrackRepositoryImpl(get(), androidContext(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SharedPreferencesRepository> {
        SharedPreferencesRepositoryImpl(get())
    }

    single<FavouriteTracksRepository> {
        FavouriteTracksRepositoryImpl(get(), get())
    }

    single<CreatePlaylistRepository> {
        CreatePlaylistRepositoryImpl(get(), get())
    }

}