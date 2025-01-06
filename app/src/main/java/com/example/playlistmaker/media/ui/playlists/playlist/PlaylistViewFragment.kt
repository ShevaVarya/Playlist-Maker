package com.example.playlistmaker.media.ui.playlists.playlist

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.common.utils.getCacheImagePath
import com.example.playlistmaker.databinding.FragmentPlaylistViewBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.favourite.FavouriteTracksFragment.Companion.INTENT_KEY
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnItemClickListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistViewFragment() : Fragment() {

    private val viewModel by viewModel<PlaylistViewViewModel>()

    private var _binding: FragmentPlaylistViewBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int = -1

    private lateinit var playlist: Playlist
    private lateinit var tracks: List<Track>

    private val adapter: TrackAdapter by lazy {
        TrackAdapter(
            onItemClickListener,
            onLongItemClickListener
        )
    }

    private val onItemClickListener = OnItemClickListener<Track> { item ->
        if (viewModel.clickDebounce()) {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                putExtra(INTENT_KEY, item)
            }
            startActivity(intent)
        }
    }

    private val onLongItemClickListener = OnItemClickListener<Track> { item ->
        deleteTrackFromPlaylist(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistId = requireArguments().getInt(PLAYLIST_ID_KEY, -1)
        if (playlistId == -1) {
            Toast.makeText(requireContext(), "Плейлист не найден!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.toolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.loadPlaylist(playlistId)

        viewModel.getPlaylist().observe(viewLifecycleOwner) {
            render(it.playlist, it.list, it.duration)
            playlist = it.playlist
            tracks = it.list
        }

        binding.tracksList.adapter = adapter
        binding.tracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val playlistBottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }
        //playlistBottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels / 3
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

        binding.buttonMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            renderMenu()
        }

        binding.buttonShare.setOnClickListener {
            share()
        }

        binding.sharePlaylist.setOnClickListener {
            share()
        }

        binding.editPlaylist.setOnClickListener { }

        binding.deletePlaylist.setOnClickListener { }


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
        var string =
            playlist.playlistName + "\n" +
                    playlist.playlistDescription + "\n" +
                    resources.getQuantityString(
                        R.plurals.track_count,
                        playlist.countTracks,
                        playlist.countTracks,
                    ) + "\n"

        for (track in tracks) {
            string += "${track.artistName} - ${track.trackName} (${track.trackTimeMillis})\n"
        }
        return string
    }

    private fun deleteTrackFromPlaylist(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Хотите удалить трек?") // Описание диалога
            .setNegativeButton("Нет") { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton("Да") { dialog, which -> // Добавляет кнопку «Да»
                viewModel.deleteTrackFromPlaylist(track, playlist)
            }
            .create()

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

        val file = File(getCacheImagePath(requireContext()), playlist.imagePath ?: "")

        Glide.with(requireContext())
            .load(file.toUri())
            .placeholder(R.drawable.placeholder)
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop()
                    )
                )
            )
            .into(binding.root.findViewById(R.id.bottom_sheet_playlist_image))
    }

    private fun render(playlist: Playlist, listTracks: List<Track>, duration: String) {
        adapter.tracks.clear()
        adapter.tracks.addAll(listTracks)
        adapter.notifyDataSetChanged()

        val file = File(getCacheImagePath(requireContext()), playlist.imagePath ?: "")

        Glide.with(requireContext())
            .load(file.toUri())
            .placeholder(R.drawable.placeholder)
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop()
                    )
                )
            )
            .into(binding.playlistImage)

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

    companion object {
        const val PLAYLIST_ID_KEY = "PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ID_KEY to playlistId)
    }
}