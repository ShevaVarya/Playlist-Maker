package com.example.playlistmaker.media.ui.playlists.playlist

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.databinding.FragmentPlaylistViewBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.favourite.FavouriteTracksFragment.Companion.INTENT_KEY
import com.example.playlistmaker.media.ui.models.OpeningAction
import com.example.playlistmaker.media.ui.models.PlaylistViewState
import com.example.playlistmaker.media.ui.playlists.createPlaylist.CreatePlaylistFragment
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnItemClickListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistViewFragment() : Fragment() {

    private val viewModel by viewModel<PlaylistViewViewModel>()

    private var _binding: FragmentPlaylistViewBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int = -1

    private lateinit var playlist: Playlist
    private lateinit var tracks: List<Track>
    private lateinit var imageUri: Uri

    private var isClickable = true

    private val adapter: TrackAdapter by lazy {
        TrackAdapter(
            onItemClickListener, onLongItemClickListener
        )
    }

    private val onItemClickListener = OnItemClickListener<Track> { item ->
        if (viewModel.clickDebounce()) {
            if (isClickable) {
                val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                    putExtra(INTENT_KEY, item)
                }
                startActivity(intent)
            }
        }
    }

    private val onLongItemClickListener = OnItemClickListener<Track> { item ->
        if (isClickable) showConfirmationDialog(
            getString(R.string.delete_track_title),
            getString(R.string.delete_track_text),
            getString(R.string.yes),
            getString(R.string.no)
        ) { viewModel.deleteTrackFromPlaylist(item, playlist) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = requireArguments().getInt(PLAYLIST_ID_KEY, -1)
        if (playlistId == -1) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_was_not_found),
                Toast.LENGTH_SHORT
            ).show()
            closeFragment()
        }

        binding.toolbar.setOnClickListener {
            closeFragment()
        }

        viewModel.loadPlaylist(playlistId)

        viewModel.getPlaylist().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.tracksList.adapter = adapter
        binding.tracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val playlistBottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        playlistBottomSheetBehavior.isHideable = false

        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        isClickable = true
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        isClickable = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        binding.buttonMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            renderMenu()
        }

        binding.buttonShare.setOnClickListener {
            share()
        }

        binding.sharePlaylist.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if (tracks.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.there_are_not_tracks),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                share()
            }
        }

        binding.editPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_createPlaylistFragment,
                CreatePlaylistFragment.createArgs(OpeningAction.UpdatePlaylist(playlistId))
            )
        }

        binding.deletePlaylist.setOnClickListener {
            showConfirmationDialog(
                getString(R.string.delete_playlist_title),
                getString(R.string.delete_playlist_text),
                getString(R.string.yes),
                getString(R.string.no)
            ) {
                viewModel.deletePlaylist(playlist)
            }
        }

    }

    private fun share() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/*"
            putExtra(Intent.EXTRA_TEXT, createMessage())
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent, null)
    }

    private fun createMessage(): String {
        val playlistDescription =
            if (playlist.playlistDescription.isEmpty()) "" else playlist.playlistDescription + "\n"
        var message =
            playlist.playlistName + "\n" + playlistDescription + resources.getQuantityString(
                R.plurals.track_count,
                playlist.countTracks,
                playlist.countTracks,
            ) + "\n"

        for (track in tracks) {
            message += "${tracks.indexOf(track) + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})\n"
        }
        return message
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        onPositiveClick: () -> Unit
    ) {
        val dialog =
            MaterialAlertDialogBuilder(requireContext()).setTitle(title).setMessage(message)
                .setNegativeButton(negativeButtonText) { dialog, _ ->
                    dialog.cancel()
                }.setPositiveButton(positiveButtonText) { _, _ ->
                    onPositiveClick()
                }.create()

        dialog.show()

        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        val color = ContextCompat.getColor(requireContext(), R.color.blue)

        positiveButton.setTextColor(color)
        negativeButton.setTextColor(color)
    }


    private fun renderMenu() {
        binding.root.findViewById<TextView>(R.id.bottom_sheet_playlist_name).text =
            playlist.playlistName
        binding.root.findViewById<TextView>(R.id.bottom_sheet_playlist_count).text =
            view?.resources?.getQuantityString(
                R.plurals.track_count,
                playlist.countTracks,
                playlist.countTracks,
            )


        Glide.with(requireContext()).load(imageUri).placeholder(R.drawable.placeholder).apply(
            RequestOptions().transform(
                MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(
                        Formatter.dpToPx(
                            2f, requireContext()
                        )
                    )
                )
            )
        ).into(binding.root.findViewById(R.id.bottom_sheet_playlist_image))
    }

    private fun render(state: PlaylistViewState) {
        when (state) {
            is PlaylistViewState.Empty -> {
                closeFragment()
            }

            is PlaylistViewState.PlaylistUIModel -> {
                showContent(state.playlist, state.list, state.duration, state.imageUri)
                playlist = state.playlist
                tracks = state.list
                playlistId = state.playlist.playlistId
                imageUri = state.imageUri
            }
        }

    }

    private fun showContent(
        playlist: Playlist,
        listTracks: List<Track>,
        duration: String,
        imageUri: Uri
    ) {
        if (listTracks.isEmpty()) {
            binding.emptyList.visibility = View.VISIBLE
            binding.tracksList.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.tracksList.visibility = View.VISIBLE
            adapter.tracks.clear()
            adapter.tracks.addAll(listTracks)
            adapter.notifyDataSetChanged()
        }


        Glide.with(requireContext()).load(imageUri).placeholder(R.drawable.placeholder).apply(
            RequestOptions().transform(
                MultiTransformation(
                    CenterCrop()
                )
            )
        ).into(binding.playlistImage)

        binding.playlistName.text = playlist.playlistName
        if (playlist.playlistDescription.isEmpty()) {
            binding.playlistDescription.visibility = View.GONE
        } else {
            binding.playlistDescription.text = playlist.playlistDescription
        }

        binding.playlistDuration.text = view?.resources?.getQuantityString(
            R.plurals.minute_count, duration.toInt(), duration.toInt()
        )
        binding.playlistCountTracks.text = view?.resources?.getQuantityString(
            R.plurals.track_count,
            playlist.countTracks,
            playlist.countTracks,
        )
    }

    private fun closeFragment() {
        findNavController().navigateUp()
    }

    companion object {
        const val PLAYLIST_ID_KEY = "PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ID_KEY to playlistId)
    }
}