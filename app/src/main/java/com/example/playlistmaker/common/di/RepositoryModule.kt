package com.example.playlistmaker.common.di

import com.example.playlistmaker.media.data.FavouriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.FileRepositoryImpl
import com.example.playlistmaker.media.data.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.media.domain.api.FileRepository
import com.example.playlistmaker.media.domain.api.PlaylistRepository
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
        SharedPreferencesRepositoryImpl(get(), get())
    }

    single<FavouriteTracksRepository> {
        FavouriteTracksRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    single<FileRepository> {
        FileRepositoryImpl(androidContext())
    }

}