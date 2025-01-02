package com.example.playlistmaker.search.data

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
        workerSharedPreferences.saveInSharedPreferences(createJsonFromTracks(tracks))
    }

    override fun getFromSharedPreferences(): List<Track> {
        val tracks = createTracksFromJson(workerSharedPreferences.gerFromSharedPreferences())
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