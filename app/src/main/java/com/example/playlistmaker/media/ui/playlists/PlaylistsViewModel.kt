package com.example.playlistmaker.media.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.ui.models.PlaylistState
import com.example.playlistmaker.search.ui.SearchViewModel.Companion.CLICK_DEBOUNCE_DELAY
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val state = MutableLiveData<PlaylistState>()
    fun getState(): LiveData<PlaylistState> = state

    private var isCLickAllowed = true

    init {
        loadPlaylist()
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            interactor.getPlaylists().collect { list ->
                if (list.isEmpty()) {
                    state.postValue(PlaylistState.EmptyPlaylists)
                } else {
                    state.postValue(PlaylistState.Content(list))
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

}