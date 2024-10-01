package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.PlayerState

interface PlayerInteractor {
    var playerState: PlayerState

    interface StateConsumer {
        fun consume(playerState: PlayerState)
    }

    fun preparePlayer(consumer: StateConsumer) {}
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int

}