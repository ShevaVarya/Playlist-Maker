package com.example.playlistmaker.media.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.ui.models.FavouriteState
import com.example.playlistmaker.media.ui.models.PlaylistState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val state = MutableLiveData<PlaylistState>()
    fun getState(): LiveData<PlaylistState> = state

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
}