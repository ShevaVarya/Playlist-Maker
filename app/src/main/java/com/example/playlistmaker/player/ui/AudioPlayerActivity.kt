package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(track.previewUrl)
    }

    private lateinit var track: Track

    private val binding: ActivityAudioPlayerBinding by lazy {
        ActivityAudioPlayerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        track = createItemFromJson(intent.getStringExtra("TRACK") ?: "", Track::class.java)
        track = viewModel.updateFavourite(track)

        binding.toolbar.setOnClickListener {
            finish()
        }

        viewModel.getPlayerState().observe(this) { playerState ->
            updateState(playerState)
        }

        viewModel.getPlayerPosition().observe(this) { position ->
            binding.recordTime.text = position
        }

        viewModel.getFavouriteValue().observe(this) { isFavourite ->
            if (isFavourite == true)
                binding.ibAddToFavorite.setImageResource(R.drawable.ic_favourite_filled)
            else if (isFavourite == false)
                binding.ibAddToFavorite.setImageResource(R.drawable.ic_favourite)
            track.isFavourite = isFavourite ?: track.isFavourite
        }

        render()

        binding.ibPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.ibAddToFavorite.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }
    }

    private fun render() {
        Glide.with(this)
            .load(Formatter.getCoverArtwork(track.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(Formatter.dpToPx(8f, this)))
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

            binding.ibAddToFavorite.setImageResource(
                if (isFavourite) R.drawable.ic_favourite_filled
                else R.drawable.ic_favourite
            )

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