package com.example.playlistmaker.media.ui.playlists.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.common.utils.debounce
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.favourite.FavouriteTracksViewModel.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.media.ui.models.PlaylistViewState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistViewViewModel(
    private val interactor: PlaylistInteractor,
) : ViewModel() {

    private val playlistState = MutableLiveData<PlaylistViewState>()

    fun getPlaylist(): LiveData<PlaylistViewState> = playlistState

    private var isCLickAllowed = true

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            interactor.getPlaylistById(playlistId).collect { playlist ->
                interactor.getPlaylistTracks(playlist.listTracksId).collect { tracks ->
                    playlistState.postValue(
                        PlaylistViewState.PlaylistUIModel(
                            playlist,
                            tracks,
                            getDuration(tracks)
                        )
                    )
                }
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            interactor.deleteTrackFromPlaylist(track.trackId, playlist).collect { updatedPlaylist ->
                interactor.getPlaylistTracks(playlist.listTracksId).collect { tracks ->
                    playlistState.postValue(
                        PlaylistViewState.PlaylistUIModel(
                            updatedPlaylist,
                            tracks,
                            getDuration(tracks)
                        )
                    )
                }
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.deletePlaylist(playlist)
            playlistState.postValue(PlaylistViewState.Empty)
        }
    }

    private fun getDuration(list: List<Track>): String {
        val totalDuration = list.sumOf { track ->
            val (min, sec) = track.trackTimeMillis.split(":").map { it.toInt() }
            (min * 60 + sec) * 1000L
        }
        return Formatter.toMinute(totalDuration)
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