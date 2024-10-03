package com.example.playlistmaker.common.di

import android.content.Context.MODE_PRIVATE
import android.media.MediaPlayer
import com.example.playlistmaker.common.Utils.SHARED_PREFERENCES_NAME_FILE
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.settings.data.workers.WorkerSharedPreferences
import com.example.playlistmaker.settings.data.workers.WorkerSharedPreferencesImpl
import com.example.playlistmaker.sharing.data.workers.ExternalNavigator
import com.example.playlistmaker.sharing.data.workers.intents.OpenBrowser
import com.example.playlistmaker.sharing.data.workers.intents.OpenBrowserImpl
import com.example.playlistmaker.sharing.data.workers.intents.SendEmail
import com.example.playlistmaker.sharing.data.workers.intents.SendEmailImpl
import com.example.playlistmaker.sharing.data.workers.intents.SendMessage
import com.example.playlistmaker.sharing.data.workers.intents.SendMessageImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<MediaPlayer> {
        MediaPlayer()
    }

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        androidContext().getSharedPreferences(SHARED_PREFERENCES_NAME_FILE, MODE_PRIVATE)
    }

    single<WorkerSharedPreferences> {
        WorkerSharedPreferencesImpl(get())
    }

    single<OpenBrowser> {
        OpenBrowserImpl()
    }

    single<SendEmail> {
        SendEmailImpl()
    }

    single<SendMessage> {
        SendMessageImpl()
    }

    single {
        ExternalNavigator(androidContext(), get(), get(), get())
    }
}