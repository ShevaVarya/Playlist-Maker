package com.example.playlistmaker.media.ui.favourite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.media.ui.models.FavouriteState
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.OnItemClickListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment() : Fragment() {

    private val viewModel by viewModel<FavouriteTracksViewModel>()

    private val adapter: TrackAdapter by lazy { TrackAdapter(onItemClickListener) }

    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!

    private val onItemClickListener = OnItemClickListener { item ->
        if (viewModel.clickDebounce()) {
            val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                putExtra(INTENT_KEY, createJson(item))
            }
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavouriteState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.tracksList.adapter = adapter
        binding.tracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun render(state: FavouriteState) {
        when (state) {
            is FavouriteState.EmptyFavourite -> showStub()
            is FavouriteState.ContentSearch -> showContent(state.tracks)
        }
    }

    private fun showStub() {
        with(binding) {
            tracksList.visibility = View.GONE
            groupForError.visibility = View.VISIBLE
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            tracksList.visibility = View.VISIBLE
            groupForError.visibility = View.GONE
        }
        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun createJson(item: Any): String {
        return Gson().toJson(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavouriteTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FavouriteTracksFragment()
        const val INTENT_KEY = "TRACK"
    }
}