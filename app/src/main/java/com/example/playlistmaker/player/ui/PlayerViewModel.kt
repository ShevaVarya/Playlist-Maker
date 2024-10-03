package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val playerState = MutableLiveData<PlayerState>(PlayerState.STATE_DEFAULT)
    private val playerPosition = MutableLiveData(INITIAL_NUMBER_FOR_PLAYER)

    fun getPlayerState(): LiveData<PlayerState> = playerState
    fun getPlayerPosition(): LiveData<Int> = playerPosition

    private val handler = Handler(Looper.getMainLooper())

    init {
        onPreparePlayer()
    }

    private fun startTimer() {
        handler.post(timerRunnable())

    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable())
    }

    private fun timerRunnable(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState.value == PlayerState.STATE_PLAYING) {
                    playerPosition.postValue(playerInteractor.getCurrentPosition())
                    handler.postDelayed(this, DELAY_MS)
                }
            }
        }
    }

    private fun onPreparePlayer() {
        playerInteractor.preparePlayer(object : PlayerInteractor.StateConsumer {
            override fun consume(playerState: PlayerState) {
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
        playerState.postValue(PlayerState.STATE_PAUSED)
        stopTimer()
    }

    private fun onStartPlayer() {
        playerInteractor.startPlayer()
        playerState.postValue(PlayerState.STATE_PLAYING)
        startTimer()
    }

    fun onReleasePlayer() {
        playerInteractor.releasePlayer()
        playerState.postValue(PlayerState.STATE_DEFAULT)
        stopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        onReleasePlayer()
    }

    companion object {

        private const val DELAY_MS = 500L
        private const val INITIAL_NUMBER_FOR_PLAYER = 0

    }
}