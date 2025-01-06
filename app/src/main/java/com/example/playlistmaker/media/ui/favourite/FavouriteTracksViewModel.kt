package com.example.playlistmaker.media.ui.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.media.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.media.ui.models.FavouriteState
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val favouriteTrackInteractor: FavouriteTrackInteractor,
) : ViewModel() {

    private val favouriteState = MutableLiveData<FavouriteState>()
    fun getFavouriteState(): LiveData<FavouriteState> = favouriteState

    private var isCLickAllowed = true

    init {
        loadFavouriteTracks()
    }

    fun loadFavouriteTracks() {
        viewModelScope.launch {
            favouriteTrackInteractor.getFavouriteTracks().collect { tracks ->
                if (tracks.isEmpty()) {
                    favouriteState.postValue(FavouriteState.EmptyFavourite)
                } else {
                    favouriteState.postValue(FavouriteState.ContentSearch(tracks))
                }
            }
        }
    }

    fun clickDebounce(): Boolean {
        val current = isCLickAllowed
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isCLickAllowed = false
        }
        isCLickAllowed = true
        return current
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}