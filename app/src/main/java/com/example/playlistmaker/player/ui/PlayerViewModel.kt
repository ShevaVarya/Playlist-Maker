package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val playerState = MutableLiveData(PlayerState.STATE_DEFAULT)
    private val playerPosition = MutableLiveData(INITIAL_NUMBER_FOR_PLAYER)

    fun getPlayerState(): LiveData<PlayerState> = playerState
    fun getPlayerPosition(): LiveData<String> = playerPosition

    private var timerJob: Job? = null

    init {
        onPreparePlayer()
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