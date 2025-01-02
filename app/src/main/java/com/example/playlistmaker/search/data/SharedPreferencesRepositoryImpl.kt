package com.example.playlistmaker.search.data

import com.example.playlistmaker.common.utils.GsonConverter
import com.example.playlistmaker.common.utils.Utils.MAX_SIZES_SEARCH_HISTORY
import com.example.playlistmaker.common.utils.WorkerSharedPreferences
import com.example.playlistmaker.search.domain.api.SharedPreferencesRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesRepositoryImpl(
    private val workerSharedPreferences: WorkerSharedPreferences,
) : SharedPreferencesRepository {
    override fun saveInSharedPreferences(tracks: List<Track>) {
        val track = GsonConverter.createJsonFromList(tracks)
        workerSharedPreferences.saveInSharedPreferences(track)
    }

    override fun getFromSharedPreferences(): List<Track> {
        val itemType = object : TypeToken<List<Track>>() {}.type
        val tracks =
        GsonConverter.createListFromJson<Track>(workerSharedPreferences.gerFromSharedPreferences(), itemType)
        return tracks
    }

    override fun clearSharedPreferences() {
        workerSharedPreferences.clearSharedPreferences()
    }

    override fun addTrackToSharedPreferences(item: Track): List<Track> {
        val tracks = getFromSharedPreferences().toMutableList()

        tracks.removeIf { it.trackId == item.trackId }

        if (tracks.size == MAX_SIZES_SEARCH_HISTORY) {
            tracks.removeAt(tracks.size - 1)
        }
        tracks.add(0, item)

        saveInSharedPreferences(tracks)

        return tracks
    }
}