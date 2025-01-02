package com.example.playlistmaker.common.di

import android.content.Context.MODE_PRIVATE
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.common.utils.Utils.SHARED_PREFERENCES_NAME_FILE
import com.example.playlistmaker.common.utils.WorkerSharedPreferences
import com.example.playlistmaker.common.utils.WorkerSharedPreferencesImpl
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.converters.TrackDbConverter
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
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

    factory<MediaPlayer> {
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

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    factory {
        TrackDbConverter()
    }

    factory {
        PlaylistDbConverter()
    }
}