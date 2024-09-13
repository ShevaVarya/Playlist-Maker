package com.example.playlistmaker.data

import com.example.playlistmaker.data.workers.WorkerSharedPreferences
import com.example.playlistmaker.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesRepositoryImpl(
    private val workerSharedPreferences: WorkerSharedPreferences
) : SharedPreferencesRepository {
    override fun saveInSharedPreferences(tracks: List<Track>) {
        workerSharedPreferences.saveInSharedPreferences(createJsonFromTracks(tracks))
    }

    override fun gerFromSharedPreferences(): List<Track> {
        return createTracksFromJson(workerSharedPreferences.gerFromSharedPreferences())
    }

    override fun clearSharedPreferences() {
        workerSharedPreferences.clearSharedPreferences()
    }

    override fun createJsonFromTracks(tracks: List<Track>): String {
        return Gson().toJson(tracks)
    }

    override fun createTracksFromJson(json: String?): List<Track> {
        if (json != null) {
            val itemType = object : TypeToken<ArrayList<Track>>() {}.type
            return Gson().fromJson(json, itemType)
        } else {
            return emptyList()
        }
    }
}