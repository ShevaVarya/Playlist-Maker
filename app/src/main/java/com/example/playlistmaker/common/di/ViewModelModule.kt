package com.example.playlistmaker.common.di

import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { (trackUrl: String) ->
        PlayerViewModel(get { parametersOf(trackUrl) })
    }

    viewModel {
        SearchViewModel(androidContext(), get())
    }
    viewModel {
        SettingsViewModel(androidContext(), get())
    }
}