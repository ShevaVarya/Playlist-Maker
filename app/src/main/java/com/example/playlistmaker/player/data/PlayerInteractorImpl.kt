package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.runBlocking

class PlayerInteractorImpl(
    private val mediaPlayer: MediaPlayer,
    private val trackUrl: String,
    private val appDatabase: AppDatabase
) : PlayerInteractor {

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

    override fun updateFavourite(track: Track): Track {
        val trackIds = runBlocking {
            appDatabase.trackDao().getAllId()
        }
        track.isFavourite = trackIds.contains(track.trackId)

        return track
    }
}