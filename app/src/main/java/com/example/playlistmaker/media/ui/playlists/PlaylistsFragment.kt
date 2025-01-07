package com.example.playlistmaker.media.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.models.OpeningGoal
import com.example.playlistmaker.media.ui.models.PlaylistState
import com.example.playlistmaker.media.ui.playlists.createPlaylist.CreatePlaylistFragment
import com.example.playlistmaker.media.ui.playlists.playlist.PlaylistViewFragment
import com.example.playlistmaker.search.ui.OnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment() : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val adapter: PlaylistsAdapter by lazy { PlaylistsAdapter(onItemClickListener) }

    private val onItemClickListener = OnItemClickListener<Playlist> { item ->
        if (viewModel.clickDebounce()) {
            findNavController().navigate(
                R.id.action_mediaFragment_to_playlistFragment,
                PlaylistViewFragment.createArgs(item.playlistId)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.listPlaylists.adapter = adapter
        binding.listPlaylists.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaFragment_to_createPlaylistFragment,
                CreatePlaylistFragment.createArgs(OpeningGoal.CreatePlaylist)
            )
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.EmptyPlaylists -> showStub()
            is PlaylistState.Content -> showContent(state.playlists)
        }

    }

    private fun showContent(playlists: List<Playlist>) {
        binding.groupForError.visibility = View.GONE
        binding.listPlaylists.visibility = View.VISIBLE

        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
    }

    private fun showStub() {
        binding.groupForError.visibility = View.VISIBLE
        binding.listPlaylists.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylist()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}