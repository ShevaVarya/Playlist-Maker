package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.common.utils.getParcelableCompat
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.models.OpeningAction
import com.example.playlistmaker.media.ui.playlists.createPlaylist.CreatePlaylistFragment
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnItemClickListener
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

    private val adapter by lazy { BottomSheetPlaylistAdapter(onItemClickListener) }

    private val onItemClickListener = OnItemClickListener<Playlist> {
        viewModel.addTrackToPlaylist(track, it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        track = getParcelableCompat<Track>(intent, TRACK_EXTRA)!!
        track = viewModel.updateFavourite(track)

        binding.bottomSheetList.adapter = adapter
        binding.bottomSheetList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.getPlayerState().observe(this) { playerState ->
            updateState(playerState)
        }

        viewModel.getPlayerPosition().observe(this) { position ->
            binding.recordTime.text = position
        }

        viewModel.getFavouriteValue().observe(this) { isFavourite ->
            if (isFavourite == true) binding.ibAddToFavorite.setImageResource(R.drawable.ic_favourite_filled)
            else if (isFavourite == false) binding.ibAddToFavorite.setImageResource(R.drawable.ic_favourite)
            track.isFavourite = isFavourite ?: track.isFavourite
        }

        viewModel.getPlaylistList().observe(this) {
            adapter.playlistList.clear()
            adapter.playlistList.addAll(it)
            adapter.notifyDataSetChanged()
        }

        viewModel.getAddingTrackState().observe(this) { result ->
            if (result.first) {
                Toast.makeText(
                    this,
                    getString(R.string.added_to_playlist) + " ${result.second.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.was_added_to_playlist) + " ${result.second.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }

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

        binding.bottomSheetNewPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.main.visibility = View.GONE
            binding.playerFragmentContainer.visibility = View.VISIBLE

            val fragment = CreatePlaylistFragment().apply {
                arguments = CreatePlaylistFragment.createArgs(OpeningAction.CreatePlaylist)
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.player_fragment_container, fragment)
                .addToBackStack("CreatePlaylistFragment").commit()
        }

        supportFragmentManager.setFragmentResultListener(CreatePlaylistFragment.REQUEST_KEY, this) { _, result ->
            if (result.getBoolean(CreatePlaylistFragment.CLOSED, false)) {
                binding.main.visibility = View.VISIBLE
                binding.playerFragmentContainer.visibility = View.GONE
            }
            viewModel.loadPlaylist()
        }
    }

    private fun render() {
        Glide.with(this).load(Formatter.getCoverArtwork(track.artworkUrl100)).centerCrop()
            .transform(RoundedCorners(Formatter.dpToPx(8f, this)))
            .placeholder(R.drawable.placeholder).into(binding.trackImage)

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
            PlayerState.STATE_PAUSED, PlayerState.STATE_PREPARED -> binding.ibPlay.setImageDrawable(
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

    companion object {
        const val TRACK_EXTRA = "TRACK"
    }
}