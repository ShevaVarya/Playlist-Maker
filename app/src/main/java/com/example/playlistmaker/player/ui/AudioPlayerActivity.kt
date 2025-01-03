package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.common.utils.GsonConverter
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private val adapter by lazy { BottomSheetPlaylistAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        track = GsonConverter.createItemFromJson(
            intent.getStringExtra("TRACK") ?: "",
            Track::class.java
        )
        track = viewModel.updateFavourite(track)

        binding.bottomSheetList.adapter = adapter
        binding.bottomSheetList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

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

        viewModel.getPlaylistList().observe(this) {
            adapter.playlistList.clear()
            adapter.playlistList.addAll(it)
            adapter.notifyDataSetChanged()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        })

        render()

        binding.toolbar.setOnClickListener {
            finish()
        }

        binding.ibPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.ibAddToFavorite.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        binding.ibAddToPlaylist.setOnClickListener {
            viewModel.loadPlaylist()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onReleasePlayer()
    }
}