package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState

class SearchViewModel(
    private val application: Context,
    private val searchInteractor: SearchInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private var isCLickAllowed = true

    private val stateLiveData = MutableLiveData<SearchState>()
    fun getSearchState(): LiveData<SearchState> = stateLiveData

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun searchDebounce(editText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { loadTracks(editText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun clickDebounce(): Boolean {
        val current = isCLickAllowed
        if (isCLickAllowed) {
            isCLickAllowed = false
            handler.postDelayed({ isCLickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun isEmptyHistory(): Boolean {
        val tracks = searchInteractor.getFromSharedPreferences()
        return tracks.isEmpty()
    }

    fun addTrackToHistory(item: Track): List<Track> {
        val tracks = getFromSharedPreferences().toMutableList()

        tracks.remove(item)

        if (tracks.size == MAX_SIZES_SEARCH_HISTORY) {
            tracks.removeLast()
        }
        tracks.add(0, item)


        saveInSharedPreferences(tracks)

        return tracks
    }

    fun clearSharedPreferences() {
        searchInteractor.clearSharedPreferences()
    }

    fun getFromSharedPreferences(): List<Track> {
        return searchInteractor.getFromSharedPreferences()
    }

    fun saveInSharedPreferences(tracks: List<Track>) {
        searchInteractor.saveInSharedPreferences(tracks)
    }

    private fun loadTracks(editText: String) {
        searchInteractor.searchTracks(
            editText,
            object : SearchInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    val tracks = mutableListOf<Track>()
                    if (foundTracks != null) {
                        tracks.clear()
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                SearchState.Error(
                                    errorMessageTitle = application.getString(R.string.connection_problem),
                                    errorMessageSubtitle = application.getString(R.string.connection_problem_additional)
                                )
                            )
                        }

                        tracks.isEmpty() -> {
                            renderState(
                                SearchState.NotFound(
                                    message = application.getString(R.string.not_found)
                                )
                            )
                        }

                        else -> {
                            renderState(
                                SearchState.ContentSearch(
                                    tracks = tracks
                                )
                            )
                        }
                    }
                }
            })
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val MAX_SIZES_SEARCH_HISTORY = 10

        private val SEARCH_REQUEST_TOKEN = Any()
    }
}