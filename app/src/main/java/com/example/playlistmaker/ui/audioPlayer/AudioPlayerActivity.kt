package com.example.playlistmaker.ui.audioPlayer

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.constants.PlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.common.util.Formatter

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val DELAY_MS = 500L
    }

    private lateinit var handler: Handler

    private lateinit var backButton: ImageView
    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var trackArtist: TextView
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var addToFavoriteButton: ImageButton
    private lateinit var trackTime: TextView
    private lateinit var recordTime: TextView
    private lateinit var trackCollectionName: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var groupOfFieldCollection: Group

    private lateinit var track: Track

    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("TRACK", Track::class.java)?.let { track = it }
        } else {
            intent.getParcelableExtra<Track>("TRACK")?.let { track = it }
        }

        backButton = findViewById(R.id.icon_back)
        trackImage = findViewById(R.id.track_image)
        trackTitle = findViewById(R.id.track_name)
        trackArtist = findViewById(R.id.track_artist)
        addToPlaylistButton = findViewById(R.id.ib_add_to_playlist)
        playButton = findViewById(R.id.ib_play)
        addToFavoriteButton = findViewById(R.id.ib_add_to_favorite)
        trackTime = findViewById(R.id.track_time)
        recordTime = findViewById(R.id.record_time)
        trackCollectionName = findViewById(R.id.collection_name)
        trackYear = findViewById(R.id.year)
        trackGenre = findViewById(R.id.genre)
        trackCountry = findViewById(R.id.country)
        groupOfFieldCollection = findViewById(R.id.group_collection)

        backButton.setOnClickListener {
            finish()
        }

        handler = Handler(Looper.getMainLooper())

        preparePlayer()

        Glide.with(this)
            .load(Formatter.getCoverArtwork(track.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(Formatter.dpToPx(2f, this)))
            .placeholder(R.drawable.placeholder)
            .into(trackImage)

        track.apply {
            trackTitle.text = trackName
            trackArtist.text = artistName
            trackTime.text = trackTimeMillis
            if (collectionName.isNullOrEmpty()) {
                groupOfFieldCollection.visibility = View.GONE
            } else {
                groupOfFieldCollection.visibility = View.VISIBLE
                trackCollectionName.text = collectionName
            }
            trackYear.text = Formatter.getYear(releaseDate)
            trackGenre.text = primaryGenreName
            trackCountry.text = country
        }

        playButton.setOnClickListener {
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
                playButton.isEnabled = true
                playerState = PlayerState.STATE_PREPARED
            }
            setOnCompletionListener {
                playButton.setImageResource(R.drawable.ic_play_arrow)
                playerState = PlayerState.STATE_PREPARED
                handler.removeCallbacks(createTimerTask())
                recordTime.setText(R.string.time_0_00)
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = PlayerState.STATE_PLAYING
        handler.post(createTimerTask())
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play_arrow)
        playerState = PlayerState.STATE_PAUSED
        handler.removeCallbacks(createTimerTask())
    }

    private fun createTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == PlayerState.STATE_PLAYING) {
                    recordTime.text = Formatter.msToMinute(mediaPlayer.currentPosition.toLong())
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