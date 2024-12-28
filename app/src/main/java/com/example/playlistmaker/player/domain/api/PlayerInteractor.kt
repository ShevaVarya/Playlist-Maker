package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track

interface PlayerInteractor {

    interface StateConsumer {
        fun consume(playerState: PlayerState)
    }

    fun preparePlayer(consumer: StateConsumer) {}
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int

    fun updateFavourite(track: Track): Track
}