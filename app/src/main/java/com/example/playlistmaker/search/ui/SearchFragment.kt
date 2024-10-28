package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SearchState
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var userInput: String = DEFAULT_VALUE

    private val tracks = ArrayList<Track>()
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private val viewModel: SearchViewModel by viewModel<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSearchState().observe(viewLifecycleOwner) {
            render(it)
        }

        val onItemClickListener = OnItemClickListener { item ->
            if (viewModel.clickDebounce()) {
                val list = viewModel.addTrackToHistory(item)
                searchHistoryAdapter.tracks.clear()
                searchHistoryAdapter.tracks.addAll(list)
                searchHistoryAdapter.notifyDataSetChanged()
                val intent = Intent(requireContext(), AudioPlayerActivity::class.java).apply {
                    putExtra(INTENT_KEY, createJson(item))
                }
                startActivity(intent)
            }
        }

        searchHistoryAdapter = TrackAdapter(onItemClickListener)

        searchAdapter = TrackAdapter(onItemClickListener)

        binding.tracksList.adapter = searchAdapter
        binding.tracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.iconClearSearch.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                if (binding.editTextSearch.hasFocus() && s?.isEmpty() == true && !viewModel.isEmptyHistory()) {
                    viewModel.setContentHistory()
                }
                if (s?.isEmpty() == false) {
                    viewModel.setSearchDebounce(s.toString())
                } else {
                    viewModel.removeCallbacks()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                userInput = s.toString()
            }
        }
        binding.editTextSearch.addTextChangedListener(textWatcher)

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.editTextSearch.text.isEmpty() && !viewModel.isEmptyHistory()) {
                viewModel.setContentHistory()
            } else {
                viewModel.setContentSearch(tracks)
            }
        }

        binding.reloadButton.setOnClickListener {
            viewModel.setSearchDebounce(binding.editTextSearch.text.toString())
        }

        binding.iconClearSearch.setOnClickListener {
            binding.editTextSearch.setText("")
            searchAdapter.tracks.clear()
            searchAdapter.notifyDataSetChanged()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSharedPreferences()
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.ContentHistory -> showContentHistory(state.tracks)
            is SearchState.ContentSearch -> showContentSearch(state.tracks)
            is SearchState.EmptyHistory -> showEmptyHistory()
            is SearchState.Error -> showConnectionError(
                state.errorMessageTitle,
                state.errorMessageSubtitle
            )

            is SearchState.Loading -> showLoading()
            is SearchState.NotFound -> showNotFoundError(state.message)
        }
    }

    private fun showContentSearch(tracks: List<Track>) {
        binding.tracksList.adapter = searchAdapter

        searchAdapter.tracks.clear()
        searchAdapter.tracks.addAll(tracks)
        searchAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.GONE

        binding.historyTittle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.llTracksList.visibility = View.VISIBLE
        binding.tracksList.visibility = View.VISIBLE
    }

    private fun showContentHistory(tracks: List<Track>) {
        binding.tracksList.adapter = searchHistoryAdapter

        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.tracks.addAll(tracks)
        searchHistoryAdapter.notifyDataSetChanged()

        binding.progressBar.visibility = View.GONE
        binding.groupForError.visibility = View.GONE

        binding.historyTittle.visibility = View.VISIBLE
        binding.clearHistoryButton.visibility = View.VISIBLE
        binding.llTracksList.visibility = View.VISIBLE
        binding.tracksList.visibility = View.VISIBLE
    }

    private fun showEmptyHistory() {
        binding.progressBar.visibility = View.GONE

        binding.historyTittle.visibility = View.GONE
        binding.clearHistoryButton.visibility = View.GONE
        binding.groupForError.visibility = View.GONE

        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.iconClearSearch.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.llTracksList.visibility = View.GONE
        binding.groupForError.visibility = View.GONE
    }

    private fun showConnectionError(errorMessage: String, errorMessageSubtitle: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            llTracksList.visibility = View.GONE

            errorImage.setImageResource(R.drawable.ic_bad_connection)
            errorTittle.text = errorMessage
            errorSubtittle.text = errorMessageSubtitle

            groupForError.visibility = View.VISIBLE
            errorImage.visibility = View.VISIBLE
            errorTittle.visibility = View.VISIBLE
            errorSubtittle.visibility = View.VISIBLE
            reloadButton.visibility = View.VISIBLE
        }
    }

    private fun showNotFoundError(message: String) {
        with(binding) {
            progressBar.visibility = View.GONE
            llTracksList.visibility = View.GONE

            errorImage.setImageResource(R.drawable.ic_bad_search)
            errorTittle.text = message

            groupForError.visibility = View.VISIBLE
            errorImage.visibility = View.VISIBLE
            errorTittle.visibility = View.VISIBLE
            errorSubtittle.visibility = View.GONE
            reloadButton.visibility = View.GONE
        }
    }

    private fun createJson(item: Any): String {
        return Gson().toJson(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY, userInput)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        userInput = savedInstanceState?.getString(
            KEY,
            DEFAULT_VALUE
        ) ?: ""
        if (userInput.isNotEmpty()) {
            binding.editTextSearch.setText(userInput)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val KEY = "KEY"
        const val DEFAULT_VALUE = ""
        const val INTENT_KEY = "TRACK"
    }
}