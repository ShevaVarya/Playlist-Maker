package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val application: Context,
    private val searchInteractor: SearchInteractor,
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private var isCLickAllowed = true

    private var tracks = emptyList<Track>()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun getSearchState(): LiveData<SearchState> = stateLiveData

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private var searchDebounce: (String) -> Unit =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { editText ->
            loadTracks(editText)
        }

    fun clickDebounce(): Boolean {
        val current = isCLickAllowed
        if (isCLickAllowed) {
            isCLickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isCLickAllowed = true
            }
        }
        return current
    }

    fun isEmptyHistory(): Boolean {
        val tracks = searchInteractor.getFromSharedPreferences()
        return tracks.isEmpty()
    }

    fun addTrackToHistory(item: Track): List<Track> {
        tracks = searchInteractor.addTrackToSharedPreferences(item)
        return tracks
    }

    fun clearSharedPreferences() {
        searchInteractor.clearSharedPreferences()
        renderState(SearchState.EmptyHistory)
    }

    fun setContentHistory(): List<Track> {
        val history = searchInteractor.getFromSharedPreferences()
        renderState(SearchState.ContentHistory(history))
        return history
    }

    fun setSearchDebounce(text: String) {
        renderState(SearchState.Loading)
        searchDebounce(text)
    }

    fun setContentSearch() {
        renderState(
            SearchState.ContentSearch(
                tracks = tracks
            )
        )
    }

    fun removeCallbacks() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    private fun loadTracks(editText: String) {
        viewModelScope.launch {
            searchInteractor.searchTracks(editText).collect { pair ->
                processResult(pair.first, pair.second)
            }
        }

    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracksList = mutableListOf<Track>()
        if (foundTracks != null) {
            tracksList.clear()
            tracksList.addAll(foundTracks)
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

            tracksList.isEmpty() -> {
                renderState(
                    SearchState.NotFound(
                        message = application.getString(R.string.not_found)
                    )
                )
            }

            else -> {
                tracks = tracksList
                setContentSearch()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L

        private val SEARCH_REQUEST_TOKEN = Any()
    }
}