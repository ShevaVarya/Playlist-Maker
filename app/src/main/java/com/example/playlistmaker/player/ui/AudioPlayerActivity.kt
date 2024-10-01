package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.util.Formatter
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel

    private lateinit var track: Track

    private var mediaPlayer = MediaPlayer()

    private val binding: ActivityAudioPlayerBinding by lazy {
        ActivityAudioPlayerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        track = createItemFromJson(intent.getStringExtra("TRACK") ?: "", Track::class.java)

        binding.iconBack.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(mediaPlayer, track.previewUrl)
        )[PlayerViewModel::class.java]


        viewModel.getPlayerState().observe(this) { playerState ->
            updateState(playerState)
        }

        viewModel.getPlayerPosition().observe(this) { position ->
            binding.recordTime.text = Formatter.msToMinute(position.toLong())
        }

        render()

        binding.ibPlay.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun render() {
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
    }

    private fun updateState(playerState: PlayerState) {
        when (playerState) {
            PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED
            -> binding.ibPlay.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.ic_play_arrow
                )
            )

            PlayerState.STATE_PLAYING -> binding.ibPlay.setImageDrawable(
                AppCompatResources.getDrawable(
                    this, R.drawable.ic_pause
                )
            )

            PlayerState.STATE_COMPLETED, PlayerState.STATE_DEFAULT -> {
                binding.ibPlay.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this, R.drawable.ic_play_arrow
                    )
                )
                binding.recordTime.text = getString(R.string.time_0_00)
            }
        }
    }

    private fun <T : Any> createItemFromJson(json: String, type: Class<T>): T {
        return Gson().fromJson(json, type)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onReleasePlayer()
    }
}