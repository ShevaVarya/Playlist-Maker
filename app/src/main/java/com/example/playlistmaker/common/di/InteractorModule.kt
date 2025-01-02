package com.example.playlistmaker.common.di

import com.example.playlistmaker.media.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.media.domain.implementation.FavouriteTrackInteractorImpl
import com.example.playlistmaker.player.data.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.implementation.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.implementation.SettingsInteractorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.implementation.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<PlayerInteractor> { (trackUrl: String) ->
        PlayerInteractorImpl(get(), trackUrl, get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<FavouriteTrackInteractor> {
        FavouriteTrackInteractorImpl(get())
    }
}