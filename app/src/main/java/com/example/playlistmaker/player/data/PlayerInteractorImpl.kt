package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState

class PlayerInteractorImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackUrl: String,
) : PlayerInteractor {
    override var playerState: PlayerState = PlayerState.STATE_DEFAULT

    override fun preparePlayer(consumer: PlayerInteractor.StateConsumer) {
        try {
            with(mediaPlayer) {
                mediaPlayer.reset()
                setDataSource(trackUrl)
                prepareAsync()
                setOnPreparedListener {
                    consumer.consume(PlayerState.STATE_PREPARED)
                }
                setOnCompletionListener {
                    consumer.consume(PlayerState.STATE_COMPLETED)
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            consumer.consume(PlayerState.STATE_DEFAULT)
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}