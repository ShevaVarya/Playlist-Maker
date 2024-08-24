package com.example.playlistmaker.ui.search

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SHARED_PREFERENCES_TRACKS_KEY = "tracks_key"
const val SHARED_PREFERENCES_NEW_TRACK_KEY = "new_track_key"
const val MAX_SIZES_SEARCH_HISTORY = 10

private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener


class SearchHistory(
    private val sharedPreferences: SharedPreferences,
    private val adapter: TrackAdapter
) {

    init {
        val json = sharedPreferences.getString(SHARED_PREFERENCES_TRACKS_KEY, null)
        if (json != null) {
            val tracks = createTracksFromJson(json)
            adapter.tracks.clear()
            adapter.tracks = tracks
            adapter.notifyDataSetChanged()
        }

        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SHARED_PREFERENCES_NEW_TRACK_KEY) {
                val jsonFromFile = sharedPreferences?.getString(SHARED_PREFERENCES_NEW_TRACK_KEY, null)
                if (jsonFromFile != null) {
                    val track = createTrackFromJson(jsonFromFile)
                    val position = checkAndRemoveItem(track)
                    if (position != null) adapter.notifyItemRemoved(position)
                    if (adapter.tracks.size == MAX_SIZES_SEARCH_HISTORY) {
                        adapter.tracks.removeLast()
                        adapter.notifyItemRemoved(MAX_SIZES_SEARCH_HISTORY - 1)
                    }
                    adapter.tracks.add(0, track)
                    adapter.notifyItemInserted(0)
                    saveTracks()
                }
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun addTrackToSearchHistory(track: Track) {
        sharedPreferences
            .edit()
            .putString(SHARED_PREFERENCES_NEW_TRACK_KEY, createJsonFromTrack(track))
            .apply()
    }

    fun saveTracks() {
        sharedPreferences.edit()
            .putString(SHARED_PREFERENCES_TRACKS_KEY, createJsonFromTracks(adapter.tracks))
            .apply()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit().clear().apply()
        adapter.tracks.clear()
        adapter.notifyDataSetChanged()
    }

    fun isEmptyHistory() : Boolean {
        val json = sharedPreferences.getString(SHARED_PREFERENCES_TRACKS_KEY, null)
        return if (json != null) {
            val tracks = createTracksFromJson(json)
            tracks.isEmpty()
        } else {
            true
        }

    }

    private fun checkAndRemoveItem(track: Track): Int? {
        val index = adapter.tracks.indexOf(track)
        return if (index != -1) {
            adapter.tracks.remove(track)
            index
        } else {
            null
        }
    }

    private fun createTracksFromJson(json: String): ArrayList<Track> {
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, itemType)
    }

    private fun createJsonFromTracks(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTrackFromJson(json: String): Track {
        return Gson().fromJson(json, Track::class.java)
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }
}