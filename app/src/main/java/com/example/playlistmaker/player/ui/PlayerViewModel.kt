package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.media.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favouriteTrackInteractor: FavouriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val playerState = MutableLiveData(PlayerState.STATE_DEFAULT)
    private val playerPosition = MutableLiveData(INITIAL_NUMBER_FOR_PLAYER)
    private val isFavourite = MutableLiveData<Boolean>()
    private val playlistList = MutableLiveData<List<Playlist>>()

    fun getPlayerState(): LiveData<PlayerState> = playerState
    fun getPlayerPosition(): LiveData<String> = playerPosition
    fun getFavouriteValue(): LiveData<Boolean?> = isFavourite
    fun getPlaylistList(): LiveData<List<Playlist>> = playlistList

    private var timerJob: Job? = null

    init {
        onPreparePlayer()
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavourite) {
                favouriteTrackInteractor.deleteTrackFromFavourite(track)
            } else {
                favouriteTrackInteractor.addTrackToFavourite(track)
            }
            val value = !track.isFavourite
            isFavourite.postValue(value)
        }
    }

    fun updateFavourite(track: Track): Track {
        return playerInteractor.updateFavourite(track)
    }

    fun loadPlaylist() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { list ->
                if (list.isNotEmpty()) {
                    playlistList.postValue(list)
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (playerState.value == PlayerState.STATE_PLAYING) {
                delay(DELAY_MS)
                playerPosition.postValue(
                    Formatter.msToMinute((playerInteractor.getCurrentPosition()).toLong())
                )
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        playerPosition.postValue(
            Formatter.msToMinute((playerInteractor.getCurrentPosition()).toLong())
        )
    }

    private fun onPreparePlayer() {
        playerInteractor.preparePlayer(object : PlayerInteractor.StateConsumer {
            override fun consume(playerState: PlayerState) {
                timerJob?.cancel()
                this@PlayerViewModel.playerState.postValue(playerState)
            }
        })
        playerPosition.postValue(INITIAL_NUMBER_FOR_PLAYER)
    }

    fun playbackControl() {
        when (playerState.value) {
            PlayerState.STATE_PLAYING -> {
                onPausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                onStartPlayer()
            }

            else -> {}
        }
    }

    private fun onPausePlayer() {
        playerInteractor.pausePlayer()
        pauseTimer()
        playerState.value = PlayerState.STATE_PAUSED
    }

    private fun onStartPlayer() {
        playerInteractor.startPlayer()
        playerState.value = PlayerState.STATE_PLAYING
        startTimer()
    }

    fun onReleasePlayer() {
        playerState.postValue(PlayerState.STATE_DEFAULT)
        playerInteractor.releasePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        onReleasePlayer()
    }

    companion object {

        private const val DELAY_MS = 300L
        private const val INITIAL_NUMBER_FOR_PLAYER = "00:00"
    }
}