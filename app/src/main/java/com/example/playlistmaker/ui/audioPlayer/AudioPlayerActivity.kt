package com.example.playlistmaker.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.constants.PlayerState
import com.example.playlistmaker.common.util.Formatter
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val DELAY_MS = 500L
    }

    private lateinit var binding: ActivityAudioPlayerBinding

    private lateinit var handler: Handler

    private lateinit var track: Track

    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("TRACK", Track::class.java)?.let { track = it }
        } else {
            intent.getParcelableExtra<Track>("TRACK")?.let { track = it }
        }

        binding.iconBack.setOnClickListener {
            finish()
        }

        handler = Handler(Looper.getMainLooper())

        preparePlayer()

        Glide.with(this)
            .load(Formatter.getCoverArtwork(track.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(Formatter.dpToPx(2f, this)))
            .placeholder(R.drawable.placeholder)
            .into(binding.trackImage)

        track.apply {
            binding.trackName.text = trackName
            binding.trackArtist.text = artistName
            binding.trackTime.text = trackTimeMillis
            if (collectionName.isNullOrEmpty()) {
                binding.groupCollection.visibility = View.GONE
            } else {
                binding.groupCollection.visibility = View.VISIBLE
                binding.collectionName.text = collectionName
            }
            binding.year.text = Formatter.getYear(releaseDate)
            binding.genre.text = primaryGenreName
            binding.country.text = country
        }

        binding.ibPlay.setOnClickListener {
            playbackControl()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_DEFAULT -> {}
        }
    }

    private fun preparePlayer() {
        with(mediaPlayer) {
            setDataSource(track.previewUrl)
            prepareAsync()
            setOnPreparedListener {
                binding.ibPlay.isEnabled = true
                playerState = PlayerState.STATE_PREPARED
            }
            setOnCompletionListener {
                binding.ibPlay.setImageResource(R.drawable.ic_play_arrow)
                playerState = PlayerState.STATE_PREPARED
                handler.removeCallbacks(createTimerTask())
                binding.recordTime.setText(R.string.time_0_00)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.ibPlay.setImageResource(R.drawable.ic_pause)
        playerState = PlayerState.STATE_PLAYING
        handler.post(createTimerTask())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.ibPlay.setImageResource(R.drawable.ic_play_arrow)
        playerState = PlayerState.STATE_PAUSED
        handler.removeCallbacks(createTimerTask())
    }

    private fun createTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    binding.recordTime.text =
                        Formatter.msToMinute(mediaPlayer.currentPosition.toLong())
                    handler.postDelayed(this, DELAY_MS)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(createTimerTask())
    }
}